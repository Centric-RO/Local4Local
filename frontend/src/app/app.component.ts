import { Component, inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { SidenavService } from "./services/sidenav.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  private translate = inject(TranslateService);
  private sidenavService = inject(SidenavService);

  constructor() {
    this.translate.setDefaultLang('nl-NL');
    this.translate.use('nl-NL');
  }

  public get shouldShowSidenav(): boolean {
    return this.sidenavService.isRouteWithNavigation;
  }
}
