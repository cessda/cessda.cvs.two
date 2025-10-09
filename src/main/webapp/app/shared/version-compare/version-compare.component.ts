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
import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subject, Subscription } from 'rxjs';
import { DiffContent } from 'ngx-text-diff/lib/ngx-text-diff.model';
import { HomeService } from 'app/home/home.service';
import { HttpResponse } from '@angular/common/http';
import { EditorService } from 'app/editor/editor.service';
import { JhiEventManager, JhiEventWithContent } from 'ng-jhipster';
import { Concept } from 'app/shared/model/concept.model';

@Component({
    selector: 'jhi-version-compare',
    templateUrl: './version-compare.component.html',
    standalone: false
})
export class VersionCompareComponent implements OnInit, OnDestroy {
  @Input() notation!: string;
  @Input() langVersion1!: string; // language-version for JSON-based or simply versionID for DB based comparison
  @Input() langVersion2!: string;
  @Input() dataSource!: string;

  eventSubscriber?: Subscription;

  isOpen = false;

  contentSubject: Subject<DiffContent> = new Subject<DiffContent>();
  contentObservable$: Observable<DiffContent> = this.contentSubject.asObservable();

  constructor(
    private homeService: HomeService,
    private editorService: EditorService,
    protected eventManager: JhiEventManager,
  ) {}

  doCvCompare(): void {
    if (this.dataSource === 'json' || this.dataSource === 'JSON') {
      this.homeService
        .getVocabularyCompare(this.notation, this.langVersion1, this.langVersion2)
        .subscribe((res: HttpResponse<string[]>) => {
          this.contentSubject.next(this.setDiffContent(res.body![0], res.body![1]));
        });
    } else {
      this.editorService.getVocabularyCompare(+this.langVersion1).subscribe((response: HttpResponse<string[]>) => {
        this.contentSubject.next(this.setDiffContent(response.body![0], response.body![1]));
      });
    }
  }

  private setDiffContent(left: string, right: string): DiffContent {
    return {
      leftContent: left,
      rightContent: right,
    } as DiffContent;
  }

  toggleCompareShow(): void {
    this.isOpen = !this.isOpen;
    if (this.isOpen) {
      this.doCvCompare();
    }
  }

  ngOnInit(): void {
    this.eventSubscriber = this.eventManager.subscribe('closeComparison', (response: JhiEventWithContent<Concept>) => {
      this.isOpen = false;
    });
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventSubscriber.unsubscribe();
    }
  }
}
