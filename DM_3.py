import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import time

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
    # print(df)
    df.pop('date')
    df['time'] = pd.to_datetime(df['time'], format=FMT)
    # df = df.set_index('time')
    res = list()
    # print(df.head())
    # print(df.iat[0, 0])
    # print(df.iat[0, 1])
    for i in range(len(df)):
        res.append(Data(df.iat[i, 0], df.iat[i, 1], df.iat[i, 2], df.iat[i, 3], df.iat[i, 4], df.iat[i, 5],
                        df.iat[i, 6]))
    return res


class Task:
    def __init__(self, data, day):
        self.data = data
        self.day = day
        self.row = list()
        self.column = list()

    def work(self):
        tot = 0
        for i in range(self.day):
            tot += self.data[i].get_close_price()
        self.row.append(self.data[self.day - 1].get_date())
        self.column.append(tot / self.day)
        for i in range(self.day, len(self.data)):
            tot -= self.data[i - self.day].get_close_price()
            tot += self.data[i].get_close_price()
            self.row.append(self.data[i].get_date())
            self.column.append(tot / self.day)

    def draw(self):
        x = np.array(self.row)
        y = np.array(self.column)
        f, ax = plt.subplots()
        ax.set_title(NAME)
        ax.plot(x, y)
        # plt.show()
        name = '_' + int(time.time()).__str__()
        path = r'C:\Users\DELL\Desktop\DM\hw3' + '\\' + name + '.png'
        print(path)
        plt.savefig(path)
        plt.show()


if __name__ == '__main__':
    d = read(NAME)
    print('please input config: days')
    task = Task(d, int(input()))
    task.work()
    task.draw()
