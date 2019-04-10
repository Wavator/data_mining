import pandas as pd
import os

_data_ = r'C:\Users\DELL\Desktop\DM\Stk_1F_2016'
files = os.listdir(_data_)
FMT = '%Y-%m-%d %H:%M'
CHUNK_SIZE = 2000


def read_in(file):
    input_file = open(file, 'rb')
    data = pd.read_csv(input_file, iterator=True, header=None)
    chunks = []
    while True:
        try:
            chunks.append(data.get_chunk(CHUNK_SIZE))
        except StopIteration:
            break
    return pd.concat(chunks, ignore_index=True)


def method_1(stock, y, z):
    # print(now_file)
    if (stock + '.csv')in files:
        now_file = _data_ + '\\' + stock + '.csv'
        df = read_in(now_file)
        solution = df[(df[0] == y) & (df[1] == z)]
        if not solution.empty:
            print(df.loc[solution.index.tolist()])
        else:
            print("No data matches")
    else:
        print('No such stock in data.')


def method_2(stock, start_date, start_time, end_date, end_time):
    start = start_date + ' ' + start_time
    end = end_date + ' ' + end_time
    if (stock + '.csv') in files:
        now_file = _data_ + '\\' + stock + '.csv'
        df = read_in(now_file)
        df.columns = ['date', 'time', 'a', 'b', 'c', 'd', 'e', 'f']
        df['time'] = df['date'] + ' ' + df['time']
        df.pop('date')
        df['time'] = pd.to_datetime(df['time'], format=FMT)
        df = df.set_index('time')
        answer = df[start: end]
        if not answer.empty:
            print(stock, end=':\n')
            print(answer)
        else:
            print("No data matches")
    else:
        print('No such stock in data')


def main():
    # print(files)
    print('test 1:')
    method_1('SH000001', '2016/01/04',  '09:33')
    print('test 2:')
    method_2('SH000001', '2016/01/01', '00:00', '2016/12/31', '23:59')


if __name__ == '__main__':
    main()
