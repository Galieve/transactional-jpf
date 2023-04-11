import os
import sys
import pandas as pd
import re

from os import listdir
from os.path import isfile, join, isdir
# pd.options.mode.chained_assignment = None


def get_path_file(filename, subfolder=""):
    file_dir = os.path.dirname(os.path.abspath(__file__))
    file_path = os.path.join(file_dir, subfolder, filename)
    return file_path


def load_csv(name, subfolder=""):
    file_path = get_path_file(name, subfolder)
    return pd.read_csv(file_path, sep=';')


def getPath(folder):
    os.chdir(os.path.expanduser("../../bin/benchmarks/"))
    return os.getcwd() + "/" + folder


def process_filename(item):
    list_item = re.split('\.|:|\+', item)
    item_map = {}
    if list_item[3] == 'TrDFSearch':
        item_map['Base ISO'] = list_item[7].replace('History', '')
        item_map['True ISO'] = list_item[11].replace('History', '')
    else:
        item_map['Base ISO'] = 'Naive'
        item_map['True ISO'] = list_item[11].replace('History', '')
    return item_map


def process_path_application(path):
    list = re.split('/|-', path)
    return list[len(list) - 3] + '-' + list[len(list) - 2]

#list[len(list) - 6] + '-' + list[len(list) - 4]
def index_path_tr(path):
    list = re.split('/|-', path)
    return list, 4

def index_path_so(path):
    list = re.split('/|-', path)
    return list, 2


def process_path_trso(path, parameter):
    if parameter == 'Transaction':
        list, id = index_path_tr(path)
    else:
        list, id = index_path_so(path)
    result = {}
    subcase = list[len(list) - 1 - id]
    result['Subcase'] = subcase
    result['Case'] = list[len(list) - 2 - id] + '-' + subcase[len(subcase) - 1]
    return result


def process_file(filename, item, results):
    file = filename + "/" + item
    f = open(file, "r")
    # print(open(file, "r").read())

    for line in f:
        if line.startswith("elapsed time:"):
            list = line.split(' ')
            results['Time'] = list[len(list) - 1].replace('\n', '')
        if line.startswith('states:'):
            list = line.split(',')
            results['End States'] = list[len(list) - 1].replace('\n', '').replace('end=', '')
        if line.startswith('transactional:'):
            list = line.split(' ')
            list = list[len(list) - 1].split(',')
            results['Histories'] = list[0].replace('\n', '').replace('histories=', '')
        if line.startswith('max memory:'):
            list = line.split(' ')
            results['Memory'] = list[len(list) - 1].replace('\n', '').replace('MB', '')

    if len(results) <= 3:
        return {}
    return results

def process_file_tr_so(filename, item, parameter):
    results = process_path_trso(filename, parameter)
    file, id = item
    results[parameter] = id
    return process_file(filename, file, results)


def process_file_application(filename, item):

    results = process_filename(item)
    results['Case'] = process_path_application(filename)
    return process_file(filename, item, results)

def listFilesApplication(folder):
    path = getPath(folder)
    benchmark_names = [f for f in listdir(path) if isdir(join(path, f))]
    cases = {}
    files = {}
    for b in benchmark_names:
        path_benchmark = path + "/" + b
        cases[b] = [f for f in listdir(path_benchmark) if isdir(join(path_benchmark, f))]
        for c in cases[b]:
            path_case = path_benchmark + "/" + c
            files[path_case] = [f for f in listdir(path_case) if isfile(join(path_case, f))]

    df = pd.DataFrame(columns=['Case', 'Base ISO', 'True ISO', 'Histories', 'End States', 'Time', 'Memory'])

    for path, item in files.items():
        for i in item:
            results = process_file_application(path, i)
            if results != {}:
            #print(results)
            #print(df.shape)

                df_results = pd.DataFrame([results])
                df = pd.concat([df, df_results], ignore_index=True)

    #print(df.head())
    df = df.sort_values(by=['Case', 'Base ISO', 'True ISO'])
    df.to_csv(folder + '/data.csv', index=False, encoding='utf-8', sep=";")

def listFilesThSeScalability(folder, parameter):
    path = getPath(folder)
    benchmark_names = [f for f in listdir(path) if isdir(join(path, f))]
    cases = {}
    subcases = {}
    files = {}
    for b in benchmark_names:
        path_benchmark = path + "/" + b
        cases[b] = [f for f in listdir(path_benchmark) if isdir(join(path_benchmark, f))]
        for c in cases[b]:
            path_case = path_benchmark + "/" + c
            subcases[path_case] = [f for f in listdir(path_case) if isdir(join(path_case, f))]
            for sc in subcases[path_case]:
                path_subcase = path_case + "/" + sc
                idx = sc[0]
                files[path_subcase] = [(f, idx) for f in listdir(path_subcase) if isfile(join(path_subcase, f))]

    df = pd.DataFrame(columns=['Case', parameter, 'Histories', 'End States', 'Time', 'Memory'])

    for path, item in files.items():
        for i in item:
            results = process_file_tr_so(path, i, parameter)

            df_results = pd.DataFrame([results])
            df = pd.concat([df, df_results], ignore_index=True)

    print(df.head())
    df = df.sort_values(by=['Case', parameter, 'Subcase'])
    df.to_csv(folder + '/data.csv', index=False, encoding='utf-8', sep=";")


# print(path + " -> " + ' '.join(item))

def generate_depending_on_mode(mode):
    if mode.endswith('application-scalability'):
        listFilesApplication(mode)
    elif mode.endswith('session-scalability'):
        listFilesThSeScalability(mode, 'Session')
    elif mode.endswith('transaction-scalability'):
        listFilesThSeScalability(mode, 'Transaction')


def process_extra():
    os.chdir(os.path.expanduser("../../"))

    file = os.getcwd() + "/extra-cases.sh"
    f = open(file, "r")
    # print(open(file, "r").read())

    with open('extra-cases2.sh', 'w') as fw:
        for line in f:
            fw.write("echo " + line)
            fw.write(line)
            fw.write("")





if __name__ == "__main__":
    folder = sys.argv[1]

    print('Number of arguments:', len(sys.argv), 'arguments.')
    print('Argument List:', str(sys.argv))

    generate_depending_on_mode(folder)


