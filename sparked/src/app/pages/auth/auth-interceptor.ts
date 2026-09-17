import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from './auth-service';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
    const authService = inject(AuthService);

    if (request.url.includes('/auth/')) {
        return next(request);
    }

    const requestWithToken = () => {
        const token = authService.bearerToken();
        return token
            ? request.clone({
                setHeaders: {
                    Authorization: `${token.tokenType} ${token.accessToken}`
                }
            })
            : request;
    };

    const request$ = authService.isTokenExpired()
        ? authService.refreshToken().pipe(
            switchMap(success => success
                ? next(requestWithToken())
                : throwError(() => new Error('Authentication refresh failed')))
        )
        : next(requestWithToken());

    return request$.pipe(
        catchError(error => {
            if (error.status !== 401) {
                return throwError(() => error);
            }

            return authService.refreshToken().pipe(
                switchMap(success => success
                    ? next(requestWithToken())
                    : throwError(() => error))
            );
        })
    );
};