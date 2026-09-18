# JavaCart — Spring Boot E-Commerce Website

A full-stack Java e-commerce site: Spring Boot backend, Spring Data JPA,
Spring Security for auth, and server-rendered Thymeleaf templates.
Runs out of the box on an in-memory H2 database — no external setup needed.

## Features

- **Storefront**: browse products by category, search, view product detail pages
- **Accounts**: register/login as a customer (BCrypt-hashed passwords)
- **Cart**: add/update/remove items, quantities capped to available stock
- **Checkout**: places an order, decrements stock, snapshots price/name per line item
- **Order history**: customers can view their past orders and status
- **Admin panel** (role-gated): manage products, categories, and update order status

## Requirements

- Java 17+
- Maven 3.8+ (or use the included setup below)

## Running it

```bash
cd ecommerce
mvn spring-boot:run
```

Then open **http://localhost:8080**

On first run, `DataSeeder` populates:
- 4 categories, 12 sample products
- An admin account: **username `admin`, password `admin123`**

Register a normal account via `/register` to shop as a customer.

## Project structure

```
src/main/java/com/valo/ecommerce/
├── EcommerceApplication.java     # entry point
├── model/                        # JPA entities (Product, Category, User, Cart/OrderItem, Order)
├── repository/                   # Spring Data JPA repositories
├── service/                      # business logic (cart, orders, auth) + Spring Security glue
├── config/                       # SecurityConfig, DataSeeder
└── controller/                   # MVC controllers (storefront, auth, cart, checkout, admin)

src/main/resources/
├── templates/                    # Thymeleaf views (+ templates/admin for the admin panel)
├── static/css/style.css
└── application.properties
```

## Switching to a real database

The app defaults to H2 in-memory (data resets on every restart — fine for
demos, not for production). To use MySQL or PostgreSQL instead:

1. Add the matching driver dependency to `pom.xml` (remove the H2 one, or
   keep both if you want to switch via profiles).
2. Replace the `spring.datasource.*` lines in `application.properties`, e.g. for MySQL:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/javacart
   spring.datasource.username=youruser
   spring.datasource.password=yourpassword
   spring.jpa.hibernate.ddl-auto=update
   ```
3. Either disable `DataSeeder` (delete it or guard it with `@Profile("demo")`)
   or leave it — it only seeds when the categories table is empty.

## Known limitations (this is a learning/demo scaffold, not production-hardened)

- No payment gateway integration — checkout just records the order.
- Deleting a category that still has products assigned to it will fail on
  the database foreign key constraint; unassign products from it first.
- No pagination on the product grid or admin lists — fine for a small catalog,
  but you'll want to add `Pageable` support before this scales to hundreds of items.
- No email confirmation, password reset, or CSRF exemption review beyond the H2 console.
- Passwords are BCrypt-hashed, but there's no rate-limiting on login/register endpoints.

## Extending it

Natural next steps, roughly in order of value:
1. Add product image upload (currently just takes an image URL).
2. Add pagination + sorting to the product grid.
3. Add product reviews/ratings.
4. Add a proper payment flow (Stripe/Razorpay sandbox).
5. Move from H2 to Postgres/MySQL for real persistence.
6. Add integration tests with `spring-boot-starter-test` (already on the classpath).
