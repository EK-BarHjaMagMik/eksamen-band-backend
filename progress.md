# Backend Progress — Stügg

_Gitignored. Local only. Last updated: 2026-05-14._

---

## Architecture snapshot

Spring Boot REST API, JWT auth, JPA/H2 (or configured DB).

```
config/
  InitData.java            # seeds admin user, 5 shows, contact info on startup
controller/
  AuthController.java      # POST /api/auth/login
  ShowController.java      # GET /api/shows/upcoming, GET /api/shows/past
  ContactInfoController.java  # GET /api/contact
  admin/
    AdminUserController.java  # POST /api/admin/users (auth required)
dto/
  AuthResponse             # token, username, role
  LoginRequest             # username, password
  ShowResponse             # id, date, city, venue, ticketLink
  ContactInfoResponse      # id, email, phoneNumber, bookingNote
  UpdateContactInfoRequest # email, phoneNumber, bookingNote — ORPHANED (no endpoint uses it)
  CreateUserRequest        # username, email, password, role
  UserResponse             # id, username, email, role
entity/
  Show                     # id, date, city, venue, ticketLink
  ContactInfo              # id, email, phoneNumber, bookingNote
  User                     # id, username, email, password (hashed), role
security/
  JwtUtil / JwtFilter / SecurityConfig / SecurityUser / CustomUserDetailsService
service/
  ShowService              # getUpcomingShows(), getPastShows()
  ContactInfoService       # get(), update() — update() has no controller endpoint yet
  UserService              # createUser(), findByUsername(), existsByUsername()
```

### Seed data (InitData)
| Type        | Value |
|-------------|-------|
| Admin user  | username: `admin` / password: `admin` / role: `ROLE_ADMIN` |
| Shows (past) | Helsingør @ Elværket (2026-03-27), Albertslund @ CTMF (2026-04-02), Lyngby @ Demant Salen DTU (2026-04-10) |
| Shows (future) | TBA @ TBA (2026-06-04), Køge @ Tapperiet (2026-09-25, has ticketLink) |
| Contact info | email: stuggofficial@gmail.com / phone: +45 12 34 56 78 / bookingNote: "For booking enquiries, reach out via email." |

---

## Endpoint inventory

| Method | Endpoint                 | Auth required | Status   | Frontend wired?       |
|--------|--------------------------|---------------|----------|-----------------------|
| POST   | /api/auth/login          | No            | Done     | No (no login page yet)|
| GET    | /api/shows/upcoming      | No            | Done     | Yes — fully rendered  |
| GET    | /api/shows/past          | No            | Done     | Service only (component stub) |
| GET    | /api/contact             | No            | Done     | Partial (bookingNote only; email+phone ignored by frontend) |
| POST   | /api/admin/users         | Yes (ROLE_ADMIN) | Done  | No                    |
| PUT    | /api/admin/contact       | —             | Missing  | No — service method exists, no endpoint |

---

## Completed

### Auth & Security
- [x] JWT authentication: login → token, JwtFilter, SecurityConfig
- [x] `User` entity, `UserRepository`, `UserService`
- [x] `AdminUserController` — `POST /api/admin/users` (creates users, auth-protected)
- [x] Default admin seeded on startup (`admin` / `admin`)

### Shows — Upcoming
- [x] `Show` entity (id, date, city, venue, ticketLink)
- [x] `ShowRepository` with `findByDateAfterOrderByDateAsc`
- [x] `ShowService.getUpcomingShows()`
- [x] `GET /api/shows/upcoming` endpoint
- [x] Seed data (5 shows, mix of past/future)
- [x] Tests: `ShowControllerTest`, `ShowRepositoryTest` (upcoming)

### Shows — Past
- [x] `ShowRepository.findByDateLessThanEqualOrderByDateDesc`
- [x] `ShowService.getPastShows()`
- [x] `GET /api/shows/past` endpoint

### Contact Info
- [x] `ContactInfo` entity (id, email, phoneNumber, bookingNote)
- [x] `ContactInfoRepository` with `findTopByOrderByIdAsc`
- [x] `ContactInfoService.get()`
- [x] `GET /api/contact` endpoint
- [x] Seed data (email, phone, bookingNote)
- [x] Tests: `ContactInfoControllerTest`, `ContactInfoRepositoryTest`

---

## In Progress

### EKS-11 — See past shows (current branch)
- [ ] Tests: `ShowRepositoryTest` — add past-shows query test
- [ ] Tests: `ShowControllerTest` — add `GET /api/shows/past` test

---

## Backlog

### Shows — Admin CRUD
- [ ] `POST /api/admin/shows` — create show (auth-protected)
- [ ] `PUT /api/admin/shows/{id}` — update show
- [ ] `DELETE /api/admin/shows/{id}` — delete show

### Contact Info — Admin update
- [ ] `PUT /api/admin/contact` — update contact info (auth-protected)
  - `ContactInfoService.update()` is already implemented, just needs a controller endpoint
  - `UpdateContactInfoRequest` DTO is ready

### Photo gallery (EKS-16)
- [ ] `Photo` entity (id, url/filename, caption, showId FK?)
- [ ] `PhotoRepository`
- [ ] `GET /api/photos` endpoint
- [ ] Decide: photos linked to shows, or standalone gallery?
- [ ] `hasPhotos` boolean on `ShowResponse` — indicates whether a past show has associated photos

### News / blog
- [ ] `NewsPost` entity — no design agreed yet
- [ ] Frontend has a TODO comment for a 2×2 card grid (headline, image, date, excerpt)

---

## Known gaps / tech debt

- `UpdateContactInfoRequest` DTO and `ContactInfoService.update()` exist but have no controller endpoint — dead code until admin contact endpoint is added
- Frontend `contact-info.js` only renders `bookingNote`; `email` and `phoneNumber` from the API response are silently ignored — frontend needs a polish pass
- Admin password is `admin` in seed data — fine for exam, not for production

---

## Session log

| Date       | Work done |
|------------|-----------|
| 2026-05-12 | past shows endpoint added (df75ed4), progress.md created |
| 2026-05-14 | Cross-examined with frontend; expanded progress.md with full endpoint inventory, seed data table, dead code notes, frontend alignment gaps |