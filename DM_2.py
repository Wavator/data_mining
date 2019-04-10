import multiprocessing as mp
import random
import datetime
import numpy as np
import os
import pandas as pd


class Solver:
    def __init__(self, s):
        self.path = r'C:\Users\DELL\Desktop\DM\Stk_1F_2016' + '\\' + s
        print(self.path)
        self.name = s
        for __ in range(4):
            self.name.pop()

    def solve(self):
        df = pd.read_csv(self.path)
        count = 0
        begin = 0
        print("len" + len(df).__str__())
        for i in range(len(df)):
            if df[i][1].__str__() == '09:31':
                begin = df[i][2]
            elif df[i][1].__str__() == '15:00' and df[i][5] > begin:
                count += 1
        return self.name, count


class Worker(mp.Process):
    def __init__(self, inQ, outQ, random_seed):
        super(Worker, self).__init__(target=self.start)
        self.inQ = inQ
        self.outQ = outQ
        np.random.seed(random_seed)

    def run(self):
        while True:
            task = self.inQ.get()  # 取出任务， 如果队列为空， 这一步会阻塞直到队列有元素
            curr = Solver(task).solve()
            self.outQ.put(curr)  # 返回结果


def create_worker(num):
    for i in range(num):
        worker.append(Worker(mp.Queue(), mp.Queue(), np.random.randint(0, 10 ** 9)))
        worker[i].start()


def finish_worker():
    '''
    关闭所有子线程
    '''
    for w in worker:
        w.terminate()


if __name__ == '__main__':
    st = datetime.datetime.now()
    doc = open(r'C:\Users\DELL\PycharmProjects\data_mining\a.txt', 'w')
    np.random.seed(3)
    worker = []
    worker_num = 8
    create_worker(worker_num)
    _data_ = r'C:\Users\DELL\Desktop\DM\Stk_1F_2016'
    files = os.listdir(_data_)
    random.shuffle(files)
    # print(files.__len__())
    for i in range(files.__len__()):
        worker[i % worker_num].inQ.put(files[i])
    # print('init success')
    result = []
    for i in range(len(files)):
        # print(i)
        result.append((worker[i % worker_num].outQ.get()))
    finish_worker()
    result.sort()
    for i in result:
        print(i[0], end=': ',file=doc)
        print(i[1], file=doc)
    print(datetime.datetime.now()-st)
