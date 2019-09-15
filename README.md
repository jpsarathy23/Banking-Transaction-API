# Transaction API for banking
---
Strategies

- Synchronised Block (2PL) - `DEFAULT`
- Optimistic Transaction (Account-Transaction Binding)
- Optimistic Concurrency Protocol
---
API Endpoints
---
```
curl -X POST 'http://localhost:8080/accounts' -d 'name={NAME}' -d 'balance={INITIAL BALANCE}'
curl -X GET 'http://localhost:8080/accounts/{ID}'
curl -X POST 'http://localhost:8080/transactions' -d 'from={FromAccountId}' -d 'to={ToAccountId}' -d 'amount={AMOUNT}'
curl -X GET 'http://localhost:8080/transactions/{ID}
curl -X GET 'http://localhost:8080/transactions/status
```
