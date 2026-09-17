# Sparked

Sparked is an [Angular](https://angular.dev) application template built with [Optimus UI](https://www.npmjs.com/package/@openng/optimus-ui). It provides a ready-to-use starting point for building admin-style applications, including:

- An application shell with a responsive sidebar, topbar, footer, and a theme configurator (light/dark mode, preset and color options)
- A dashboard page with stats, charts, and sales/notification widgets
- A landing page with hero, features, highlights, and pricing sections
- Auth pages (login, access denied, error) and a 404 page
- A CRUD sample page and a full UI kit showcase (forms, tables, lists, charts, menus, overlays, and more)

## Tech stack

- [Angular 21](https://angular.dev) (standalone components)
- [Optimus UI](https://www.npmjs.com/package/@openng/optimus-ui) component library, themes, and icons
- [Tailwind CSS 4](https://tailwindcss.com)
- [Chart.js](https://www.chartjs.org) for charts

## Running locally

### Prerequisites

- [Node.js](https://nodejs.org) (version supported by Angular 21)
- [pnpm](https://pnpm.io) — this project uses pnpm as its package manager

### Installation

Clone the repository, then install the dependencies:

```bash
pnpm install
```

### Development server

Start a local development server with:

```bash
pnpm start
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

### Building

To build the project for production, run:

```bash
pnpm run build
```

The build artifacts are stored in the `dist/` directory. By default, the production build optimizes the application for performance and speed.

### Running unit tests

To execute unit tests with the [Karma](https://karma-runner.github.io) test runner, run:

```bash
pnpm test
```

### Formatting

To format the codebase with Prettier, run:

```bash
pnpm run format
```

## Authentication

The frontend uses a short-lived access token and a refresh token stored by
the browser as an HttpOnly cookie.

### Login

The login page sends the credentials to `POST /api/auth/login` with
`withCredentials: true`. The backend returns an access token in the JSON
response and sets the `refresh_token` cookie. The access token is kept only
in memory by `AuthService`; it is not written to `localStorage` or
`sessionStorage`.

The HTTP interceptor adds the access token to protected API requests:

```http
Authorization: Bearer <access-token>
```

### Reload and refresh

On application startup, `provideAppInitializer` calls
`AuthService.refreshToken()`. Angular waits for this request before the
initial route is activated, so the user sees a blank page during the initial
handshake.

- If the refresh cookie is valid, the backend rotates it and returns a new
	access token. The user is taken directly to the protected application.
- If the cookie is missing, expired, or invalid, the refresh fails silently,
	the in-memory token is cleared, and the authentication guard redirects to
	`/auth/login`.

The interceptor also refreshes the token when it is expired or when a
protected request returns `401`. The original request is retried once after
a successful refresh.

### Logout

Logout calls `POST /api/auth/logout` with `withCredentials: true`. The
backend revokes the refresh token and deletes the cookie; the frontend clears
the in-memory access token.

### Local development

The local backend runs over HTTP, so `security.refresh-cookie-secure=false`
is configured for development. In production the application must run over
HTTPS and this setting must be `true`.

### Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
pnpm exec ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
pnpm exec ng generate --help
```

## Additional resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
