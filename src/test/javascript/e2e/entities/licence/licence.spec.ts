/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { LicenceComponentsPage, LicenceDeleteDialog, LicenceUpdatePage } from './licence.page-object';

const expect = chai.expect;

describe('Licence e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let licenceComponentsPage: LicenceComponentsPage;
  let licenceUpdatePage: LicenceUpdatePage;
  let licenceDeleteDialog: LicenceDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Licences', async () => {
    await navBarPage.goToEntity('licence');
    licenceComponentsPage = new LicenceComponentsPage();
    await browser.wait(ec.visibilityOf(licenceComponentsPage.title), 5000);
    expect(await licenceComponentsPage.getTitle()).to.eq('cvsApp.licence.home.title');
    await browser.wait(ec.or(ec.visibilityOf(licenceComponentsPage.entities), ec.visibilityOf(licenceComponentsPage.noResult)), 1000);
  });

  it('should load create Licence page', async () => {
    await licenceComponentsPage.clickOnCreateButton();
    licenceUpdatePage = new LicenceUpdatePage();
    expect(await licenceUpdatePage.getPageTitle()).to.eq('cvsApp.licence.home.createOrEditLabel');
    await licenceUpdatePage.cancel();
  });

  it('should create and save Licences', async () => {
    const nbButtonsBeforeCreate = await licenceComponentsPage.countDeleteButtons();

    await licenceComponentsPage.clickOnCreateButton();

    await promise.all([
      licenceUpdatePage.setNameInput('name'),
      licenceUpdatePage.setLinkInput('link'),
      licenceUpdatePage.setLogoLinkInput('logoLink'),
      licenceUpdatePage.setAbbrInput('abbr')
    ]);

    expect(await licenceUpdatePage.getNameInput()).to.eq('name', 'Expected Name value to be equals to name');
    expect(await licenceUpdatePage.getLinkInput()).to.eq('link', 'Expected Link value to be equals to link');
    expect(await licenceUpdatePage.getLogoLinkInput()).to.eq('logoLink', 'Expected LogoLink value to be equals to logoLink');
    expect(await licenceUpdatePage.getAbbrInput()).to.eq('abbr', 'Expected Abbr value to be equals to abbr');

    await licenceUpdatePage.save();
    expect(await licenceUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await licenceComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Licence', async () => {
    const nbButtonsBeforeDelete = await licenceComponentsPage.countDeleteButtons();
    await licenceComponentsPage.clickOnLastDeleteButton();

    licenceDeleteDialog = new LicenceDeleteDialog();
    expect(await licenceDeleteDialog.getDialogTitle()).to.eq('cvsApp.licence.delete.question');
    await licenceDeleteDialog.clickOnConfirmButton();

    expect(await licenceComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
