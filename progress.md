# Project Progress

## Completed

### Auth & Security
- [x] JWT authentication (login, token generation, filter)
- [x] User entity, repository, service
- [x] Admin user controller
- [x] Default admin seeded on startup

### Shows — Upcoming
- [x] `Show` entity (id, date, city, venue, ticketLink)
- [x] `ShowRepository` with `findByDateAfterOrderByDateAsc`
- [x] `ShowService.getUpcomingShows()`
- [x] `GET /api/shows/upcoming` endpoint
- [x] Seed data (5 shows, mix of past/future dates)
- [x] Unit tests: `ShowControllerTest`, `ShowRepositoryTest`

## In Progress

### Shows — Past (branch: EKS-10)
- [ ] Add `photoLink` field to `Show` entity
- [ ] Add `findByDateBeforeOrEqualOrderByDateDesc` query to `ShowRepository`
- [ ] Add `getPastShows()` to `ShowService`
- [ ] Add `GET /api/shows/past` to `ShowController`
- [ ] Update seed data with past shows that have photoLink set (or null)

## Backlog
- [ ] `ShowResponse` DTO — currently empty/unused; wire it up instead of returning raw entities
- [ ] Photo gallery feature (photos not implemented yet — just photoLink placeholder)
- [ ] Admin CRUD for shows
