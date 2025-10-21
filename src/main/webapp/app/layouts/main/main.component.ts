/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { Component, OnInit, RendererFactory2, Renderer2, inject } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, ActivatedRouteSnapshot, NavigationEnd, NavigationError } from '@angular/router';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';

import { AccountService } from 'app/core/auth/account.service';
import { zip } from 'rxjs';

@Component({
  selector: 'jhi-main',
  templateUrl: './main.component.html',
  standalone: false,
})
export class MainComponent implements OnInit {
  private accountService = inject(AccountService);
  private titleService = inject(Title);
  private router = inject(Router);
  private translateService = inject(TranslateService);

  private renderer: Renderer2;

  constructor() {
    const rootRenderer = inject(RendererFactory2);

    this.renderer = rootRenderer.createRenderer(document.querySelector('html'), null);
  }

  ngOnInit(): void {
    // try to log in automatically
    this.accountService.identity().subscribe();

    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.updateTitle();
      }
      if (event instanceof NavigationError && event.error.status === 404) {
        this.router.navigate(['/404']);
      }
    });

    this.translateService.onLangChange.subscribe((langChangeEvent: LangChangeEvent) => {
      this.updateTitle();

      this.renderer.setAttribute(document.querySelector('html'), 'lang', langChangeEvent.lang);
    });
  }

  private getPageTitle(routeSnapshot: ActivatedRouteSnapshot): string {
    let title: string = routeSnapshot.data && routeSnapshot.data['pageTitle'] ? routeSnapshot.data['pageTitle'] : '';
    if (routeSnapshot.firstChild) {
      title = this.getPageTitle(routeSnapshot.firstChild) || title;
    }
    return title;
  }

  private updateTitle(): void {
    const pageTitle = this.getPageTitle(this.router.routerState.snapshot.root);
    if (!pageTitle) {
      this.translateService.get('global.title').subscribe(title => this.titleService.setTitle(title));
    } else {
      zip(this.translateService.get(pageTitle), this.translateService.get('global.title')).subscribe(title => {
        if (title[0] == title[1]) {
          return this.titleService.setTitle(`${title[0]}`);
        } else {
          return this.titleService.setTitle(`${title[0]} - ${title[1]}`);
        }
      });
    }
  }
}
