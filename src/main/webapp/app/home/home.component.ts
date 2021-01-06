import {Component} from '@angular/core';
import {AppScope} from 'app/shared/model/enumerations/app-scope.model';

@Component({
  selector: 'jhi-home',
  template: '<jhi-vocabulary-search-result [appScope]="appScope"></jhi-vocabulary-search-result>'
})
export class HomeComponent{
  appScope: AppScope = AppScope.PUBLICATION;
  constructor() {}
}
