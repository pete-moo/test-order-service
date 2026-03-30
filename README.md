# Order Processing — Technical Test

**Time allowed:** 1 hour
**Level:** Mid-level Java / Spring Boot

---

## The Scenario

You are joining a team that runs a small e-commerce platform built as microservices.
A pre-built **Inventory Service** already exists and is running.
Your task is to build the **Order Service** that integrates with it.

---

## What Is Provided

| Component | Port | Status |
|---|---|---|
| `inventory-service` | 8081 | **Complete — start this before you begin** |
| `order-service` | 8080 | **Skeleton — you build this** |

### Starting the Inventory Service

```bash
cd inventory-service
./mvnw spring-boot:run
```

Verify it is running:
```bash
curl http://localhost:8081/inventory/BUSINESSCARD
```
Expected response:
```json
{"productId":"BUSINESSCARD","name":"Moo Business Card","quantity":50}
```

### Available Products

| Product ID     | Name | Stock |
|----------------|---|-------|
| `BUSINESSCARD` | Moo Business Card | 50    |
| `POSTCARD`     | Moo Postcard | 20    |
| `PEN`          | Moo Wonky pen | 5     |
| `STICKER`      | Moo Amazing Sticker | 300   |

---

## Inventory Service API

### Check Stock

```
GET /inventory/{productId}
```
Returns `200 OK`:
```json
{"productId":"BUSINESSCARD","name":"Moo Business Card","quantity":50}
```
Returns `404 Not Found` if product does not exist.

### Reserve Stock

```
POST /inventory/{productId}/reserve
Content-Type: application/json

{"quantity": 3}
```

Returns `200 OK` on success:
```json
{"reserved": true, "remaining": 47}
```

Returns `409 Conflict` if insufficient stock:
```json
{"reserved": false, "reason": "Insufficient stock. Available: 2"}
```

---

## Your Task: Build the Order Service

Open `order-service/` in your IDE. The skeleton is set up with some spring dependencies already configured.

---

### Notes

- Do not modify the `inventory-service`.

### Required Endpoints

#### Create Order

```
POST /orders
Content-Type: application/json

{
  "productId": "BUSINESSCARD",
  "quantity": 2,
  "customerId": "cust-123"
}
```

**Behaviour:**
1. Check the product exists in the Inventory Service
2. Attempt to reserve the requested quantity
3. Persist the order into a datastore if there's enough stock
4. Return `201 Created` with the order:

```json
{
  "orderId": "a1b2c3d4",
  "customerId": "cust-123",
  "productId": "BUSINESSCARD",
  "quantity": 2,
  "status": "CONFIRMED",
  "createdAt": "2024-11-01T10:00:00Z"
}
```

**Error cases to handle:**
- Product not found → `404`
- Insufficient stock → `409` with a clear message
- Inventory Service unreachable / times out → `503` with a clear message
- Invalid request body → `400`

#### Get Order

```
GET /orders/{orderId}
```

Returns `200 OK` with the order, or `404` if not found.