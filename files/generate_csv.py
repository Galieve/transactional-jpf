import os
import sys
import pandas as pd

#pd.options.mode.chained_assignment = None


def get_path_file(filename, subfolder=""):
    file_dir = os.path.dirname(os.path.abspath(__file__))
    file_path = os.path.join(file_dir, subfolder, filename)
    return file_path


def load_csv(name, subfolder=""):
    file_path = get_path_file(name, subfolder)
    return pd.read_csv(file_path, sep=';')


if __name__ == "__main__":


    print('Number of arguments:', len(sys.argv), 'arguments.')
    print('Argument List:', str(sys.argv))

    # save_times_histogram()
    # plt.rc('text', usetex=True)
    # font = {'family':'serif','size':16}
    # font = {'family': 'serif', 'size': 16, 'serif': ['computer modern roman']}
    # plt.rc('font', **font)
    # plt.rc('legend', **{'fontsize': 14})
    #labels = ["\\texttt{CC}", "\\texttt{CC} + \\texttt{SI}", "\\texttt{CC} + \\texttt{SER}", "\\texttt{RA} + \\texttt{CC}", "\\texttt{RC} + \\texttt{CC}", "\\texttt{true} + \\texttt{CC}", "DFS(\\texttt{CC})"]
    #plot_cactus("app-scala", 'Time', labels)
    #plot_cactus_mem("app-scala", 'Mem', labels)
    #plot_cactus_histories("app-scala", labels)
    #plot_scalability('th-scala', 'sessions')
    #plot_scalability('tr-scala', 'transactions per session')
    #calculate_parameters("app-scala", labels)
    #calculate_parameters_2('th-scala')
    #calculate_parameters_2('tr-scala')
