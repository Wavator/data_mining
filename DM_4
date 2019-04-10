import random

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import os

PATH = r'C:\Users\DELL\Desktop\DM\Stk_1F_2016'
FMT = '%Y-%m-%d %H:%M'
NAME = 'SH000001'


class Data(object):
    def __init__(self, date, open_price, high_price, low_price, end_price, total_money, total_amount):
        # print(len(df))
        self.date = date
        self.open_price = open_price
        self.high_price = high_price
        self.low_price = low_price
        self.end_price = end_price
        self.total_money = total_money
        # print(self.date)
        self.total_amount = total_amount

    def get_open_price(self):
        return self.open_price

    def get_close_price(self):
        return self.end_price

    def get_high_price(self):
        return self.high_price

    def get_low_price(self):
        return self.low_price

    def get_date(self) -> pd.datetime:
        return self.date


def read(stock):
    path = PATH + '\\' + stock + '.csv'
    df = pd.read_csv(path)
    df.columns = ['date', 'time', 'a', 'b', 'c', 'd', 'e', 'f']
    df['time'] = df['date'] + ' ' + df['time']
    df.pop('date')
    df['time'] = pd.to_datetime(df['time'], format=FMT)
    res = list()
    for i in range(len(df)):
        res.append(Data(df.iat[i, 0], df.iat[i, 1], df.iat[i, 2], df.iat[i, 3], df.iat[i, 4], df.iat[i, 5],
                        df.iat[i, 6]))
    return res


def calculate_sim(a, b):
    tot = 0
    for i in range(min(len(a), len(b))):
        if a[i] == b[i]:
            tot += 1
    return tot / len(a)


# 0-20, 20-50, 50-80, 80-100
DAY = 7


def solve(stock):
    data = read(stock)
    res = []
    pos_dm = [0]
    for i in range(1, len(data)):
        pos_dm.append(max(0, data[i].get_high_price() - data[i - 1].get_high_price()))
    low_dm = [0]
    for i in range(1, len(data)):
        low_dm.append(max(0, data[i - 1].get_low_price() - data[i].get_low_price()))
    c = 0
    for i in range(7):
        if pos_dm[i] >= low_dm[i]:
            c += 1
    for i in range(7, len(data)):
        if pos_dm[i - 7] >= low_dm[i - 7]:
            c -= 1
        if pos_dm[i] >= low_dm[i]:
            c += 1
        if c > 5:
            res.append(1)
        elif c > 2:
            res.append(0)
        else:
            res.append(-1)
    return res


def work(stockA, stockB):
    a = solve(stockA)
    b = solve(stockB)
    print('Similarity of %s and %s is %.2f' % (stockA, stockB, 100 * calculate_sim(a, b)), end='%\n')


if __name__ == '__main__':
    files = os.listdir(PATH)
    # print(len(files))
    for i in range(6):
        x = random.randint(0, len(files) - 1)
        y = random.randint(0, len(files) - 1)
        while x == y:
            y = random.randint(0, len(files) - 1)
        # print(x, y)
        work(files[x].split('.csv')[0], files[y].split('.csv')[0])
