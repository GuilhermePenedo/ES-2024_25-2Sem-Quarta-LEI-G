import csv

with open('../Dados/Madeira-Moodle-1.1.csv') as csv_file:
    csv_read=csv.reader(csv_file, delimiter=',')
