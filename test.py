import sys
import requests
import random
import json

accounts = range(1,10)
for id in accounts:
    data = {'name':'test', 'balance':1000}
    requests.post('http://localhost:8080/accounts', data)
transactions = range(1,10000)
for id in transactions:
    fromId = random.randint(1,10)
    toId = random.randint(1,10)
    if fromId == toId:
        continue;
    amount = random.randint(100,200)
    data = {'from':fromId, 'to':toId, 'amount':amount}
    requests.post('http://localhost:8080/transactions', data)
total = 0
for id in accounts:
    r = requests.get('http://localhost:8080/accounts/%d' % id)
    total += r.json()['balance']
print total
