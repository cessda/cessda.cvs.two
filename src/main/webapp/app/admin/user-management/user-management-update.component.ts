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
import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { LANGUAGES } from 'app/core/language/language.constants';
import { User } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { UserAgency } from 'app/shared/model/user-agency.model';
import { HttpResponse } from '@angular/common/http';
import { Agency } from 'app/shared/model/agency.model';
import { AgencyService } from 'app/agency/agency.service';

@Component({
  selector: 'jhi-user-mgmt-update',
  templateUrl: './user-management-update.component.html',
})
export class UserManagementUpdateComponent implements OnInit {
  user!: User;
  languages = LANGUAGES;
  authorities: string[] = [];
  isSaving = false;

  agencies: Agency[] = [];

  selectedAgencyId: number;
  selectedAgencyRole: string;
  selectedLanguage: string;

  editForm = this.fb.group({
    id: [''],
    login: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(50), Validators.pattern('^[_.@A-Za-z0-9-]*')]],
    firstName: ['', [Validators.maxLength(50)]],
    lastName: ['', [Validators.maxLength(50)]],
    email: ['', [Validators.minLength(5), Validators.maxLength(254), Validators.email]],
    activated: [false],
    langKey: ['en'],
    authorities: [['']],
  });

  constructor(
    private userService: UserService,
    private route: ActivatedRoute,
    private agencyService: AgencyService,
    private fb: FormBuilder,
  ) {
    this.selectedAgencyId = 1;
    this.selectedAgencyRole = 'ADMIN_TL';
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
              : 0,
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
        sort: ['name,asc'],
      })
      .subscribe((res: HttpResponse<Agency[]>) => {
        this.agencies = res.body || [];
        this.selectedAgencyId = this.agencies[0].id || 1;
      });
  }

  userAgencyToCompare(ua: UserAgency): string {
    return ua.agencyId! + ua.agencyRole! + (ua.language ? ua.language : '');
  }

  getAgencyName(agencyId: number): string {
    if (this.agencies.length === 0) {
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
        () => this.onSaveError(),
      );
    } else {
      this.userService.create(this.user).subscribe(
        () => this.onSaveSuccess(),
        () => this.onSaveError(),
      );
    }
  }

  private updateForm(user: User): void {
    this.editForm.patchValue({
      id: user.id ? String(user.id) : '',
      login: user.login,
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email,
      activated: user.activated,
      langKey: user.langKey,
      authorities: user.authorities,
    });
  }

  private updateUser(user: User): void {
    user.login = this.editForm.controls.login.value!;
    user.firstName = this.editForm.controls.firstName.value!;
    user.lastName = this.editForm.controls.lastName.value!;
    user.email = this.editForm.controls.email.value!;
    user.activated = this.editForm.controls.activated.value!;
    user.langKey = this.editForm.controls.langKey.value!;
    user.authorities = this.editForm.controls.authorities.value || [];
  }

  private onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  private onSaveError(): void {
    this.isSaving = false;
  }

  deleteAgencyRole(ua: UserAgency): void {
    if (
      confirm(
        'Are you sure to delete agency role ' +
          this.getAgencyName(ua.agencyId!) +
          ': ' +
          ua.agencyRole +
          (ua.language ? '-' + ua.language : '') +
          '? The agency-role deletion will only be completed after the form is saved.',
      )
    ) {
      if (this.user.userAgencies) {
        const index = this.user.userAgencies.indexOf(ua);
        if (index > -1) {
          this.user.userAgencies.splice(index, 1);
        }
      }
    }
  }

  saveAgencyRole(): void {
    if (
      confirm(
        'Are you sure to add agency role ' +
          this.getAgencyName(this.selectedAgencyId) +
          ': ' +
          this.selectedAgencyRole +
          (this.selectedLanguage !== '' ? '-' + this.selectedLanguage : '') +
          '? The agency-role addition will only be completed after the form is saved.',
      )
    ) {
      const userAgency: UserAgency = {
        userId: this.user.id,
        agencyRole: this.selectedAgencyRole as UserAgency['agencyRole'],
        agencyId: this.selectedAgencyId,
        agencyName: this.getAgencyName(this.selectedAgencyId),
        language: this.selectedLanguage !== '' ? this.selectedLanguage : undefined,
      };
      if (!this.user.userAgencies) {
        this.user.userAgencies = [];
      }
      this.user.userAgencies.push(userAgency);
    }
  }

  getValue(event: Event): string {
    return (event.target as HTMLSelectElement).value;
  }
}
