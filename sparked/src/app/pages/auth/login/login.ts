import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ButtonModule } from '@openng/optimus-ui/button';
import { CheckboxModule } from '@openng/optimus-ui/checkbox';
import { InputTextModule } from '@openng/optimus-ui/inputtext';
import { PasswordModule } from '@openng/optimus-ui/password';
import { RippleModule } from '@openng/optimus-ui/ripple';
import { AppFloatingConfigurator } from '../../../layout/component/app.floatingconfigurator';
import { Toast } from '@openng/optimus-ui/toast';
import { AuthService } from '../auth-service';
import { Router } from '@angular/router';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [ButtonModule, CheckboxModule, InputTextModule, PasswordModule, FormsModule, RouterModule, RippleModule, AppFloatingConfigurator, Toast],
    changeDetection: ChangeDetectionStrategy.Eager,
    templateUrl: './login.html'
})
export class Login {
    authService = inject(AuthService)
    private router = inject(Router);

    isLoginRunning = signal<boolean>(false);

    email: string = '';

    password: string = '';

    checked: boolean = false;

    onSignIn(): void {
        this.isLoginRunning.set(true);

        this.authService.loginUser(this.email, this.password).subscribe({
            next: () => {
                this.router.navigate(['/']);
                this.isLoginRunning.set(false);
            },
            error: () => {
                this.isLoginRunning.set(false);
            }
        });
    }
}
