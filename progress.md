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

### Shows — Past (branch: EKS-11-See-past-shows)
- [x] Add `findByDateLessThanEqualOrderByDateDesc` query to `ShowRepository`
- [x] Add `getPastShows()` to `ShowService`
- [x] Add `GET /api/shows/past` to `ShowController`
- [ ] Tests: `ShowRepositoryTest` and `ShowControllerTest` for past shows

## Backlog
- [ ] `hasPhotos` boolean on `ShowResponse` — next user story, indicates whether a past show has photos
- [ ] Photo gallery feature — separate user story, internal gallery linked to shows
- [ ] Admin CRUD for shows

## Notes
- `ShowResponse` DTO is wired up and used (records-based, `fromEntity` static factory)
- 3 of 5 seed shows are already in the past (dates before 2026-05-12): Helsingør, Albertslund, Lyngby
