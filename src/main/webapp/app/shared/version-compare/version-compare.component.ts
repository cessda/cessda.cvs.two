import { Component, Input, OnInit } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { DiffContent } from 'ngx-text-diff/lib/ngx-text-diff.model';
import { HomeService } from 'app/home/home.service';
import { HttpResponse } from '@angular/common/http';
import { EditorService } from 'app/editor/editor.service';

@Component({
  selector: 'jhi-version-compare',
  templateUrl: './version-compare.component.html'
})
export class VersionCompareComponent implements OnInit {
  @Input() notation!: string;
  @Input() langVersion1!: string; // language-version for JSON-based or simply versionID for DB based comparison
  @Input() langVersion2!: string;
  @Input() dataSource!: string;

  isOpen = false;

  contentSubject: Subject<DiffContent> = new Subject<DiffContent>();
  contentObservable$: Observable<DiffContent> = this.contentSubject.asObservable();

  constructor(private homeService: HomeService, private editorService: EditorService) {}

  ngOnInit(): void {
    this.doCvCompare();
  }

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
}
