import csv
import os
import time
from datetime import datetime
from math import log10

from matplotlib.ticker import MaxNLocator

import pandas as pd
import ast
import numpy as np
import warnings
import matplotlib.pyplot as plt
from matplotlib import collections as matcoll
import matplotlib.ticker as tick
import seaborn as sns
from textwrap import wrap
from mpl_toolkits.axes_grid1 import make_axes_locatable
pd.options.mode.chained_assignment = None


def get_path_file(filename, subfolder=""):
    file_dir = os.path.dirname(os.path.abspath(__file__))
    file_path = os.path.join(file_dir, subfolder, filename)
    return file_path


def load_csv(name, subfolder=""):
    file_path = get_path_file(name, subfolder)
    return pd.read_csv(file_path, sep=';')


def set_font(names):
    plt.rcParams["font.family"] = names


def time_to_int(time_string):
    h, m, s = time_string.split(':')
    return int(h) * 3600 + int(m) * 60 + int(s)


def time_to_int_nan(time_string):
    t = time_to_int(time_string)
    if t >= 1800:
        return np.nan
    else:
        return t


def values_if_nan(h, e, t, m):
    if np.isnan(t):
        return np.nan, np.nan, np.nan
    else:
        return int(h), int(e), int(m[0:-2])


def prepare_dataframe_cactus(file, n):
    df = load_csv(file + ".csv")
    for i in range(1, n):
        df['Time' + str(i)] = df['Time' + str(i)].map(lambda x: time_to_int_nan(x) / 60)
        df['Histories' + str(i)], df['End states' + str(i)], df['Mem.' + str(i)] = df.apply(lambda r: values_if_nan(r['Histories' + str(i)], r['End states' + str(i)], r['Time' + str(i)], r['Mem.' + str(i)]), axis=1).str

        df['Histories' + str(i)] = df['Histories' + str(i)].map(lambda x: x/1000)
        df['End states' + str(i)] = df['End states' + str(i)].map(lambda x: x/1000)
        df['Mem.' + str(i)] = df['Mem.' + str(i)].map(lambda x: (x)/1024)
    return df


def prepare_dataframe_parameters(file, n):
    df = load_csv(file + ".csv")
    for i in range(1, n):
        df['Time' + str(i)] = df['Time' + str(i)].map(lambda x: time_to_int_nan(x))
        df['Histories' + str(i)], df['End states' + str(i)], df['Mem.' + str(i)] = df.apply(lambda r: values_if_nan(r['Histories' + str(i)], r['End states' + str(i)], r['Time' + str(i)], r['Mem.' + str(i)]), axis=1).str

        df['Histories' + str(i)] = df['Histories' + str(i)].map(lambda x: x/1000)
        df['End states' + str(i)] = df['End states' + str(i)].map(lambda x: x/1000)
        df['Mem.' + str(i)] = df['Mem.' + str(i)].map(lambda x: (x))
    return df


def prepare_dataframe_scalability(file, n):
    df = load_csv(file + ".csv")

    for i in range(1, n):
        df['Time' + str(i)] = df['Time' + str(i)].map(lambda x: time_to_int(x) )
        df['Histories' + str(i)], df['End states' + str(i)], df['Mem.' + str(i)] = df.apply(lambda r: values_if_nan(r['Histories' + str(i)], -1, r['Time' + str(i)], r['Mem.' + str(i)]), axis=1).str

        df['Histories' + str(i)] = df['Histories' + str(i)].map(lambda x: x/1000)
        df['Mem.' + str(i)] = df['Mem.' + str(i)].map(lambda x: (x )/1024)
    return df


def plot_cactus(file, field, labels):
    n = len(labels) + 1

    df = prepare_dataframe_cactus(file, n)

    fig = plt.figure()
    #plt.subplots_adjust(left=None, bottom=None, right=None, top=None, wspace=None, hspace=None)

    ax = fig.add_axes((0.1, 0.1, 0.85, 0.85))

    plt.rc('figure', titlesize=20)
    ax.set_xlabel('Number of benchmarks')
    ax.set_ylabel('Time (min)')
    plt.gca().set_prop_cycle(plt.cycler('color', plt.cm.get_cmap('Dark2_r')(np.linspace(0, 1, n))))
    plt.xlim([0 - 0.25, len(df.index) - 1 + 0.25])

    for i_s in range(1, n):
        i = str(i_s)
        algo = df[['Benchmark', 'Histories' + i, 'End states' + i, 'Time' + i, 'Mem.' + i]]
        algo = algo.sort_values(by=[field + i])
        algo['Time' + i] = algo['Time' + i].cumsum()
        # print(algo)
        # lines = [(i, t) for (i,t) in zip( algo['Time'+i], df.index)]

        plt.plot(df.index, algo[field + i], label=labels[i_s - 1], linewidth=2)

    plt.legend(loc="upper right")

    print('Saving...')
    fig.savefig('eps/' + file + '-' + field + '.eps', format='eps', bbox_inches='tight')
    # plt.show()

    print('Saved! :)')

    return


def plot_cactus_mem(file, field, labels):
    n = len(labels) + 1

    df = prepare_dataframe_cactus(file, n)

    fig = plt.figure()
    ax = fig.add_axes((0.15, 0.15, 0.8, 0.8))

    plt.rc('figure', titlesize=20)
    #plt.subplots_adjust(left=None, bottom=None, right=None, top=None, wspace=None, hspace=None)

    ax.set_xlabel('Number of benchmarks')
    ax.set_ylabel('Memory (GB)')
    plt.gca().set_prop_cycle(plt.cycler('color', plt.cm.get_cmap('Dark2_r')(np.linspace(0, 1, n))))
    plt.xlim([0 - 0.25, len(df.index) - 1 + 0.25])
    plt.ylim([0 - 0.25, 8.5 + 0.25])


    for i_s in range(1, n):
        i = str(i_s)
        algo = df[['Benchmark', 'Histories' + i, 'End states' + i, 'Time' + i, 'Mem.' + i]]
        algo = algo.sort_values(by=[field+ '.' + i])
        algo['Mem' + i] = algo['Mem.' + i].cumsum()
        # print(algo)
        # lines = [(i, t) for (i,t) in zip( algo['Time'+i], df.index)]

        plt.plot(df.index, algo[field + i], label=labels[i_s - 1], linewidth=2)

    plt.legend(loc="lower right")

    print('Saving...')

    fig.savefig('eps/' + file + '-' + field + '.eps', format='eps', bbox_inches='tight')
    # plt.show()

    print('Saved! :)')

    return


def plot_cactus_histories(file, labels):
    n = len(labels) + 1

    df = prepare_dataframe_cactus(file, n)

    fig = plt.figure()
    ax = fig.add_axes((0.1, 0.1, 0.85, 0.85))

    plt.rc('figure', titlesize=20)
    #plt.subplots_adjust(left=0.1, bottom=None, right=None, top=None, wspace=None, hspace=None)

    ax.set_xlabel('Number of benchmarks')
    ax.set_ylabel('Number of histories ($\\times 10^3$)')
    plt.gca().set_prop_cycle(plt.cycler('color', plt.cm.get_cmap('Dark2_r')(np.linspace(0, 1, n))))
    plt.xlim([0 - 0.25, len(df.index) - 1 + 0.25])

    for i_s in range(1, n):
        i = str(i_s)
        if i_s > 1:
            df_labels = ['Benchmark', 'Histories' + i, 'End states' + i, 'Time' + i, 'Mem.' + i, 'Histories1']
        else:
            df_labels = ['Benchmark', 'Histories' + i, 'End states' + i, 'Time' + i, 'Mem.' + i]
        algo = df[df_labels]
        if i_s <= 3:
            field = 'Histories'
        else:
            field = 'End states'

        algo = algo.sort_values(by=[field+i])

        algo[field + i] = algo[field + i].cumsum()

        plt.plot(df.index, algo[field + i], label=labels[i_s - 1], linewidth=2)

    plt.legend(loc="upper right")

    print('Saving...')
    fig.savefig('eps/' + file + '-' + 'histories' + '.eps', format='eps', bbox_inches='tight')
    # plt.show()

    print('Saved! :)')

    return


def plot_scalability(file, object_name):
    n = 5 + 1
    df = prepare_dataframe_scalability(file, n)

    bench = len(df.index)
    fig = plt.figure()
    ax = fig.add_axes((0.15, 0.15, 0.85, 0.85))
    ax2 = ax.twinx()


    plt.rc('figure', titlesize=20)
    ax.set_xlabel('Number of ' + object_name)
    ax.set_ylabel('Time (mins)')
    ax2.set_ylabel('Memory (GB)', labelpad=10)
    ax.xaxis.set_major_locator(MaxNLocator(integer=True))
    ax.yaxis.set_major_locator(MaxNLocator(integer=True))
    ax2.yaxis.set_major_locator(MaxNLocator(integer=True))

    cmap = plt.cm.get_cmap('Dark2')


    plt.xlim([1 - 0.05, 5 + 0.05])
    ax2.set_ylim([0 - 0.25, 8 + 0.25])

    #plt.ylim([0 - 0.25, 30 + 0.25])

    memory = [df['mem.' + str(i)].mean() for i in range(1, n)]
    time = [df['Time' + str(i)].mean() / 60 for i in range(1, n)]

    t_lab = ax.plot(range(1, n), time, label='Avg. time', linewidth=4, color=cmap(4/8-0.05))
    m_lab = ax2.plot(range(1, n), memory, label='Avg. memory', linewidth=4, color=cmap(1/8-0.05))
    lns = t_lab + m_lab
    labs = [l.get_label() for l in lns]
    plt.legend(lns, labs, loc="upper left")

    print('Saving...')
    fig.savefig('eps/' + file + '.eps', format='eps', bbox_inches='tight')

    print('Saved! :)')

    return


def calculate_parameters(file, labels):
    n = len(labels) + 1

    df = prepare_dataframe_parameters(file, n)


    results = {}
    for i_s in range(1, n):
        i = str(i_s)
        if i_s > 1:
            algo = df[['Benchmark', 'Histories' + i, 'End states' + i, 'Time' + i, 'Mem.' + i, 'Time1']]
            algo.loc[:, 'SpeedUp'+i] = algo['Time'+i] / algo['Time1']
            results['SpeedUp'+i+'-average'] = algo['SpeedUp'+i].mean()
        else:
            algo = df[['Benchmark', 'Histories' + i, 'End states' + i, 'Time' + i, 'Mem.' + i]]

            results['SpeedUp'+i+'-average'] = 1

        results['Time'+i+'-average'] = algo['Time'+i].map(lambda x: x).mean().squeeze()

        #results['Time'+i+'-min'] = algo['Time'+i].min()
        #results['Time'+i+'-max'] = algo['Time'+i].max()
        #results['SpeedUp'+i+'-min'] = algo['SpeedUp'+i].min()
        #results['SpeedUp'+i+'-max'] = algo['SpeedUp'+i].max()
        #results['Mem.'+i+'-min'] = algo['Mem.'+i].min()
        #results['Mem.'+i+'-max'] = algo['Mem.'+i].max()
        results['Mem.'+i+'-average'] = algo['Mem.'+i].mean()
        if i_s <= 3:
            field = 'Histories'
        else:
            field = 'End states'

        algo[field+i] = algo[field+i].map(lambda x: x)


        results['Histories'+i+'-average'] = algo[field+i].mean()
        #results['Histories'+i+'-min'] = algo[field+i].min()
        #results['Histories'+i+'-max'] = algo[field+i].max()

    results = {k: "%.2f" % v for k, v in results.items()}
    print(results)

    with open('results.csv', 'w') as f:  # You will need 'wb' mode in Python 2.x
        w = csv.DictWriter(f, results.keys())
        w.writeheader()
        w.writerow(results)


def calculate_parameters_2(file):
    n = 5 + 1
    df = prepare_dataframe_scalability(file, n)

    memory = [df['Mem.' + str(i)].map(lambda x:x*1024).mean() for i in range(1, n)]
    print(np.array(memory).mean())


if __name__ == "__main__":
    # save_times_histogram()
    plt.rc('text', usetex=True)
    # font = {'family':'serif','size':16}
    font = {'family': 'serif', 'size': 16, 'serif': ['computer modern roman']}
    plt.rc('font', **font)
    plt.rc('legend', **{'fontsize': 14})
    labels = ["\\texttt{CC}", "\\texttt{CC} + \\texttt{SI}",
              "\\texttt{CC} + \\texttt{SER}", "\\texttt{RA} + \\texttt{CC}",
              "\\texttt{RC} + \\texttt{CC}", "\\texttt{true} + \\texttt{CC}", "DFS(\\texttt{CC})"]
    #plot_cactus("app-scala", 'Time', labels)
    plot_cactus_mem("app-scala", 'Mem', labels)
    #plot_cactus_histories("app-scala", labels)
    #plot_scalability('th-scala', 'sessions')
    #plot_scalability('tr-scala', 'transactions per session')
    #calculate_parameters("app-scala", labels)
    #calculate_parameters_2('th-scala')
    #calculate_parameters_2('tr-scala')


