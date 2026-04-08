# User API Spec

## Register User

- Endpoint : POST /api/users

Request Body :
```json
{
  "username" : "admin",
  "password" : "passw123",
  "name" : "Admin"
}
```

Response Body (Success) :
```json
{
  "data" : "OK"
}
```

Response Body (Failed) :

```json
{
  "error" : "Username must not blank, ???"
}
```

## Login User

- Endpoint : POST /api/auth/login

Request Body :

```json
{
  "username" : "admin",
  "password" : "passw123"
}
```

Response Body (Success) :

```json
{
  "data" : "TOKEN",
  "expiredAt" : 1800000
}
```

Response Body (Failed, 401) :

```json
{
  "error" : "Username or password is incorrect"
}
```

## Get User

- Endpoint : GET /api/users/current

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : {
    "username" : "admin",
    "name" : "Admin"
  }
}
```

Response Body (Failed, 401) :

```json
{
  "error" : "Unauthorized"
}
```

## Update User

- Endpoint : PATCH /api/users/current

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "name" : "Super Admin",
  "password" : "New password"
}
```

Response Body (Success) :

```json
{
  "data" : {
    "username" : "admin",
    "name" : "Super Admin"
  }
}
```

Response Body (Failed, 401) :

```json
{
  "error" : "Unauthorized"
}
```

## Logout User

Endpoint : DELETE /api/auth/logout

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : "OK"
}
```