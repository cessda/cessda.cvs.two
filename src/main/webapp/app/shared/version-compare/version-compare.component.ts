import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subject, Subscription } from 'rxjs';
import { DiffContent } from 'ngx-text-diff/lib/ngx-text-diff.model';
import { HomeService } from 'app/home/home.service';
import { HttpResponse } from '@angular/common/http';
import { EditorService } from 'app/editor/editor.service';
import { JhiEventManager, JhiEventWithContent } from 'ng-jhipster';
import { IConcept } from 'app/shared/model/concept.model';

@Component({
  selector: 'jhi-version-compare',
  templateUrl: './version-compare.component.html'
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

  constructor(private homeService: HomeService, private editorService: EditorService, protected eventManager: JhiEventManager) {}

  doCvCompare(): void {
    if (this.dataSource === 'json' || this.dataSource === 'JSON') {
      this.homeService
        .getVocabularyCompare(this.notation, this.langVersion1, this.langVersion2)
        .subscribe((res: HttpResponse<string[]>) => {
          const newContent: DiffContent = {
            leftContent: res.body![0],
            rightContent: res.body![1]
          };
          this.contentSubject.next(newContent);
        });
    } else {
      this.editorService.getVocabularyCompare(+this.langVersion1).subscribe((res: HttpResponse<string[]>) => {
        const newContent: DiffContent = {
          leftContent: res.body![0],
          rightContent: res.body![1]
        };
        this.contentSubject.next(newContent);
      });
    }
  }

  toggleCompareShow(): void {
    this.isOpen = !this.isOpen;
    if (this.isOpen) {
      this.doCvCompare();
    }
  }

  ngOnInit(): void {
    this.eventSubscriber = this.eventManager.subscribe('closeComparison', (response: JhiEventWithContent<IConcept>) => {
      this.isOpen = false;
    });
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventSubscriber.unsubscribe();
    }
  }
}
