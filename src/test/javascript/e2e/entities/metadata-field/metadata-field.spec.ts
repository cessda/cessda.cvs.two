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

import { MetadataFieldComponentsPage, MetadataFieldDeleteDialog, MetadataFieldUpdatePage } from './metadata-field.page-object';

const expect = chai.expect;

describe('MetadataField e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let metadataFieldComponentsPage: MetadataFieldComponentsPage;
  let metadataFieldUpdatePage: MetadataFieldUpdatePage;
  let metadataFieldDeleteDialog: MetadataFieldDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load MetadataFields', async () => {
    await navBarPage.goToEntity('metadata-field');
    metadataFieldComponentsPage = new MetadataFieldComponentsPage();
    await browser.wait(ec.visibilityOf(metadataFieldComponentsPage.title), 5000);
    expect(await metadataFieldComponentsPage.getTitle()).to.eq('cvsApp.metadataField.home.title');
    await browser.wait(
      ec.or(ec.visibilityOf(metadataFieldComponentsPage.entities), ec.visibilityOf(metadataFieldComponentsPage.noResult)),
      1000
    );
  });

  it('should load create MetadataField page', async () => {
    await metadataFieldComponentsPage.clickOnCreateButton();
    metadataFieldUpdatePage = new MetadataFieldUpdatePage();
    expect(await metadataFieldUpdatePage.getPageTitle()).to.eq('cvsApp.metadataField.home.createOrEditLabel');
    await metadataFieldUpdatePage.cancel();
  });

  it('should create and save MetadataFields', async () => {
    const nbButtonsBeforeCreate = await metadataFieldComponentsPage.countDeleteButtons();

    await metadataFieldComponentsPage.clickOnCreateButton();

    await promise.all([
      metadataFieldUpdatePage.setMetadataKeyInput('metadataKey'),
      metadataFieldUpdatePage.setDescriptionInput('description'),
      metadataFieldUpdatePage.objectTypeSelectLastOption()
    ]);

    expect(await metadataFieldUpdatePage.getMetadataKeyInput()).to.eq(
      'metadataKey',
      'Expected MetadataKey value to be equals to metadataKey'
    );
    expect(await metadataFieldUpdatePage.getDescriptionInput()).to.eq(
      'description',
      'Expected Description value to be equals to description'
    );

    await metadataFieldUpdatePage.save();
    expect(await metadataFieldUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await metadataFieldComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last MetadataField', async () => {
    const nbButtonsBeforeDelete = await metadataFieldComponentsPage.countDeleteButtons();
    await metadataFieldComponentsPage.clickOnLastDeleteButton();

    metadataFieldDeleteDialog = new MetadataFieldDeleteDialog();
    expect(await metadataFieldDeleteDialog.getDialogTitle()).to.eq('cvsApp.metadataField.delete.question');
    await metadataFieldDeleteDialog.clickOnConfirmButton();

    expect(await metadataFieldComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
