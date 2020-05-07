import { Component, Input } from '@angular/core';
import { IConcept } from 'app/shared/model/concept.model';
import VocabularyUtil from 'app/shared/util/vocabulary-util';

@Component({
  selector: 'jhi-tree',
  templateUrl: './tree.component.html',
  styleUrls: ['./tree.component.scss']
})
export class TreeComponent {
  @Input() parentNotation?: string;
  @Input() conceptList?: IConcept[];
  @Input() level?: number;

  removeCurrentLevelItems: any = (conceptList?: IConcept[], parentNotation?: string, level?: number) => {
    return conceptList!.filter(c => c.parent !== parentNotation);
  };

  isConceptHasChildren(notation?: string, conceptList?: IConcept[]): boolean {
    return VocabularyUtil.isConceptHasChildren(notation!, conceptList!);
  }
}
