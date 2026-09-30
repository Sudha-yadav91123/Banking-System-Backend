# Banking Management System — JSP to React Migration

## Frontend
The original 8 JSP pages have been recreated as React pages:
- Login
- Register
- User Dashboard
- Transactions
- Admin Dashboard
- Admin Customers
- Admin Accounts
- Admin Transactions

The React UI preserves the original glassmorphism/background style and responsive layouts while replacing JSP forms/links with Axios API calls.

## Run backend
```bash
mvn spring-boot:run
```

## Run frontend
```bash
cd frontend
npm install
npm run dev
```

Frontend: http://localhost:5173  
Backend: http://localhost:8080

The frontend uses `withCredentials: true` because the migrated backend still uses the existing HTTP session authentication.
