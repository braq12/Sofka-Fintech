import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import {
  provideHttpClient,
  withFetch,
  withInterceptors,
} from '@angular/common/http';
import { interceptorError } from './core/interceptor-error.interceptor';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { routes } from './app.routes';
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeng/themes/aura';
import { MessageService } from 'primeng/api';
import { provideHighcharts } from 'highcharts-angular'; 

export const appConfig: ApplicationConfig = {
  providers: [
    providePrimeNG({
            theme: {
                preset: Aura
            }
        }),
    provideRouter(routes),
    MessageService,
    provideHttpClient(withFetch(), withInterceptors([interceptorError])),
    provideAnimationsAsync(),
    provideHighcharts(),
    
  ],
};
