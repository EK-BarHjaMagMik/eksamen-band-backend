# Backend Progress — Stügg

_Gitignored. Local only. Last updated: 2026-05-18._

---

## Architecture snapshot

Spring Boot REST API, JWT auth, JPA/H2 (or configured DB).

```
config/
  InitData.java            # seeds admin user, shows, photos, contact info on startup
controller/
  AuthController.java      # POST /api/auth/login
  ShowController.java      # GET /api/shows/upcoming, /past, /{showId}
  PhotoController.java     # GET /api/photos, /api/photos/recent, /api/shows/{showId}/photos
  ContactInfoController.java  # GET /api/contact
  admin/
    AdminUserController.java  # POST /api/admin/users (auth required)
dto/
  AuthResponse             # token, username, role
  LoginRequest             # username, password
  ShowResponse             # id, date, city, venue, ticketLink, hasPhotos
  PhotoResponse            # id, url, caption, dateTaken, photographer
  ContactInfoResponse      # id, email, phoneNumber, bookingNote
  UpdateContactInfoRequest # email, phoneNumber, bookingNote — ORPHANED (no endpoint uses it)
  CreateUserRequest        # username, email, password, role
  UserResponse             # id, username, email, role
entity/
  Show                     # id, date, city, venue, ticketLink
  Photo                    # id, url, caption, dateTaken, photographer, createdAt, show (ManyToOne)
  ContactInfo              # id, email, phoneNumber, bookingNote
  User                     # id, username, email, password (hashed), role
security/
  JwtUtil / JwtFilter / SecurityConfig / SecurityUser / CustomUserDetailsService
service/
  ShowService              # getUpcomingShows(), getPastShows(), getShowById()
  PhotoService             # getPhotos(), getRecentPhotos(int), getPhotosByShowId(Long)
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
| GET    | /api/shows/upcoming           | No               | Done    | Yes — fully rendered  |
| GET    | /api/shows/past               | No               | Done    | Service only (component stub) |
| GET    | /api/shows/{showId}           | No               | Done    | No |
| GET    | /api/contact                  | No               | Done    | Partial (bookingNote only; email+phone ignored by frontend) |
| GET    | /api/photos                   | No               | Done    | No |
| GET    | /api/photos/recent            | No               | Done    | No |
| GET    | /api/shows/{showId}/photos    | No               | Done    | No |
| POST   | /api/admin/users              | Yes (ROLE_ADMIN) | Done    | No                    |
| PUT    | /api/admin/contact            | —                | Missing | No — service method exists, no endpoint |

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

### EKS-28 — See individual band member information (current branch)
- [ ] EKS-78: `Member` entity (name, role, bio, displayOrder)
- [ ] EKS-79: `MemberRepository`, `MemberService`
- [ ] EKS-80: `GET /api/members` endpoint — sorted by displayOrder
- [ ] EKS-81: Seed Stügg members in `InitData`

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

### Photo gallery (EKS-12) — DONE (merged 2026-05-18)
- [x] `Photo` entity (id, url, caption, dateTaken, photographer, show FK with ON DELETE SET NULL)
- [x] `PhotoRepository`
- [x] `GET /api/photos`, `/api/photos/recent`, `/api/shows/{showId}/photos` endpoints
- [x] `hasPhotos` on `ShowResponse`
- [x] Seed data: 10 sample photos, last 5 linked to UHØRT show

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
| 2026-05-18 | Photo gallery (EKS-12) merged; show error handling + tests added; starting EKS-28 (Member entity) |