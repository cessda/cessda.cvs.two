import {Component, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';

import {LANGUAGES} from 'app/core/language/language.constants';
import {User} from 'app/core/user/user.model';
import {UserService} from 'app/core/user/user.service';
import {IUserAgency, UserAgency} from 'app/shared/model/user-agency.model';
import {HttpResponse} from '@angular/common/http';
import {IAgency} from 'app/shared/model/agency.model';
import {AgencyService} from 'app/agency/agency.service';
import VocabularyUtil from 'app/shared/util/vocabulary-util';

@Component({
  selector: 'jhi-user-mgmt-update',
  templateUrl: './user-management-update.component.html'
})
export class UserManagementUpdateComponent implements OnInit {
  user!: User;
  languages = LANGUAGES;
  authorities: string[] = [];
  isSaving = false;

  agencies?: IAgency[];

  selectedAgencyId?: number;
  selectedAgencyRole?: string;
  selectedLanguage?: string;

  editForm = this.fb.group({
    id: [],
    login: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(50), Validators.pattern('^[_.@A-Za-z0-9-]*')]],
    firstName: ['', [Validators.maxLength(50)]],
    lastName: ['', [Validators.maxLength(50)]],
    email: ['', [Validators.minLength(5), Validators.maxLength(254), Validators.email]],
    activated: [],
    langKey: [],
    authorities: []
  });

  constructor(
    private userService: UserService,
    private route: ActivatedRoute,
    private agencyService: AgencyService,
    private fb: FormBuilder
  ) {
    this.selectedAgencyId = 1;
    this.selectedAgencyRole = 'CONTRIBUTOR_TL';
    this.selectedLanguage = '';
  }

  ngOnInit(): void {
    this.route.data.subscribe(({ user }) => {
      if (user) {
        this.user = user;

        this.user.userAgencies!.sort((ua1, ua2) =>
          this.userAgencyToCompare(ua1) < this.userAgencyToCompare(ua2)
            ? -1
            : this.userAgencyToCompare(ua1) > this.userAgencyToCompare(ua2)
            ? 1
            : 0
        );

        if (this.user.id === undefined) {
          this.user.activated = true;
        }
        this.updateForm(user);
      }
    });
    this.userService.authorities().subscribe(authorities => {
      this.authorities = authorities;
    });
    this.agencyService
      .query({
        page: 0,
        size: 1000,
        sort: ['name,asc']
      })
      .subscribe((res: HttpResponse<IAgency[]>) => {
        this.agencies = res.body!;
        this.selectedAgencyId = this.agencies[0].id!;
      });
  }

  userAgencyToCompare(ua: IUserAgency): string {
    return ua.agencyId! + ua.agencyRole! + (ua.language ? ua.language : '');
  }

  getAgencyName(agencyId: number): string {
    if (this.agencies === undefined) {
      return agencyId + '';
    }
    return this.agencies.filter(a => a.id === agencyId)[0].name!;
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    this.updateUser(this.user);
    if (this.user.id !== undefined) {
      this.userService.update(this.user).subscribe(
        () => this.onSaveSuccess(),
        () => this.onSaveError()
      );
    } else {
      this.userService.create(this.user).subscribe(
        () => this.onSaveSuccess(),
        () => this.onSaveError()
      );
    }
  }

  private updateForm(user: User): void {
    this.editForm.patchValue({
      id: user.id,
      login: user.login,
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email,
      activated: user.activated,
      langKey: user.langKey,
      authorities: user.authorities
    });
  }

  private updateUser(user: User): void {
    user.login = this.editForm.get(['login'])!.value;
    user.firstName = this.editForm.get(['firstName'])!.value;
    user.lastName = this.editForm.get(['lastName'])!.value;
    user.email = this.editForm.get(['email'])!.value;
    user.activated = this.editForm.get(['activated'])!.value;
    user.langKey = this.editForm.get(['langKey'])!.value;
    user.authorities = this.editForm.get(['authorities'])!.value;
  }

  private onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  private onSaveError(): void {
    this.isSaving = false;
  }

  getLangIsoFormatted(langIso?: string): string {
    if (langIso === undefined) return '';
    return VocabularyUtil.getLangIsoFormatted(langIso);
  }

  deleteAgencyRole(ua: IUserAgency): void {
    if (
      confirm(
        'Are you sure to delete agency role ' +
          this.getAgencyName(ua.agencyId!) +
          ': ' +
          ua.agencyRole +
          (ua.language ? '-' + this.getLangIsoFormatted(ua.language) : '') +
          '? The agency-role deletion will only be completed after the form is saved.'
      )
    ) {
      const index = this.user.userAgencies!.indexOf(ua);
      if (index > -1) {
        this.user.userAgencies!.splice(index, 1);
      }
    }
  }

  saveAgencyRole(): void {
    if (
      confirm(
        'Are you sure to add agency role ' +
          this.getAgencyName(this.selectedAgencyId!) +
          ': ' +
          this.selectedAgencyRole +
          (this.selectedLanguage !== '' ? '-' + this.getLangIsoFormatted(this.selectedLanguage) : '') +
          '? The agency-role addition will only be completed after the form is saved.'
      )
    ) {
      const userAgency = {
        ...new UserAgency(),
        userId: this.user.id,
        agencyRole: this.selectedAgencyRole,
        agencyId: this.selectedAgencyId,
        language: this.selectedLanguage !== '' ? this.selectedLanguage : undefined
      };
      this.user.userAgencies!.push(userAgency);
    }
  }
}
