import { environment } from '@/environments/environment';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { MessageService } from '@openng/optimus-ui/api';
import { Observable, catchError, map, of, tap } from 'rxjs';

export interface LoginResponse {
    accessToken: string;
    tokenType: string;
    expiresIn: number;
}

export interface Token {
    accessToken: string;
    tokenType: string;
    expiresIn: number;
    timeAcquired: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
    private http = inject(HttpClient);
    private messageService = inject(MessageService);

    readonly bearerToken = signal<Token | null>(null);

    isTokenExpired(): boolean {
        const token = this.bearerToken();
        if (!token) return true;

        return Date.now() >= token.timeAcquired + token.expiresIn * 1000;
    }

    isAuthenticated(): boolean {
        return this.bearerToken() !== null && !this.isTokenExpired();
    }

    loginUser(email: string, password: string): Observable<void> {
        return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, {
            email,
            password
        }, { withCredentials: true }).pipe(
            tap(response => this.updateToken(response)),
            map(() => undefined),
            catchError(error => {
                this.messageService.add({
                    severity: 'error',
                    summary: 'Login failed.',
                    detail: 'Wrong username or password.',
                    icon: 'pi pi-exclamation-circle'
                });
                throw error;
            })
        );
    }

    refreshToken(): Observable<boolean> {
        return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/refresh`, {}, { withCredentials: true }).pipe(
            tap(response => this.updateToken(response)),
            map(() => true),
            catchError(() => {
                this.bearerToken.set(null);
                return of(false);
            })
        );
    }

    logout(): Observable<void> {
        return this.http.post<void>(`${environment.apiUrl}/auth/logout`, {}, { withCredentials: true }).pipe(
            tap(() => this.clearToken())
        );
    }

    clearToken(): void {
        this.bearerToken.set(null);
    }

    private updateToken(response: LoginResponse) {
        this.bearerToken.set({
            accessToken: response.accessToken,
            tokenType: response.tokenType,
            expiresIn: response.expiresIn,
            timeAcquired: Date.now()
        })
    }
}
