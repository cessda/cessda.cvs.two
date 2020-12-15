import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {SERVER_API_URL} from 'app/app.constants';
import {Maintenance} from 'app/admin/maintenance/maintenance.model';

type EntityResponseType = HttpResponse<Maintenance>;

@Injectable({ providedIn: 'root' })
export class MaintenanceService {
  public maintenanceUrl = SERVER_API_URL + 'api/maintenance';

  constructor(private http: HttpClient) {}

  generateJson(): Observable<EntityResponseType> {
    return this.http.get<Maintenance>(`${this.maintenanceUrl}/publication/generate-json`, { observe: 'response' });
  }

  indexAgency(): Observable<EntityResponseType> {
    return this.http.get<Maintenance>(`${this.maintenanceUrl}/index/agency`, { observe: 'response' });
  }

  indexAgencyStats(): Observable<EntityResponseType> {
    return this.http.get<Maintenance>(`${this.maintenanceUrl}/index/agency-stats`, { observe: 'response' });
  }

  indexVocabularyPublish(): Observable<EntityResponseType> {
    return this.http.get<Maintenance>(`${this.maintenanceUrl}/index/vocabulary`, { observe: 'response' });
  }

  indexVocabularyEditor(): Observable<EntityResponseType> {
    return this.http.get<Maintenance>(`${this.maintenanceUrl}/index/vocabulary/editor`, { observe: 'response' });
  }
}
