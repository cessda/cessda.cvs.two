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
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { MetadataValueComponentsPage, MetadataValueDeleteDialog, MetadataValueUpdatePage } from './metadata-value.page-object';

const expect = chai.expect;

describe('MetadataValue e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let metadataValueComponentsPage: MetadataValueComponentsPage;
  let metadataValueUpdatePage: MetadataValueUpdatePage;
  let metadataValueDeleteDialog: MetadataValueDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load MetadataValues', async () => {
    await navBarPage.goToEntity('metadata-value');
    metadataValueComponentsPage = new MetadataValueComponentsPage();
    await browser.wait(ec.visibilityOf(metadataValueComponentsPage.title), 5000);
    expect(await metadataValueComponentsPage.getTitle()).to.eq('cvsApp.metadataValue.home.title');
    await browser.wait(
      ec.or(ec.visibilityOf(metadataValueComponentsPage.entities), ec.visibilityOf(metadataValueComponentsPage.noResult)),
      1000
    );
  });

  it('should load create MetadataValue page', async () => {
    await metadataValueComponentsPage.clickOnCreateButton();
    metadataValueUpdatePage = new MetadataValueUpdatePage();
    expect(await metadataValueUpdatePage.getPageTitle()).to.eq('cvsApp.metadataValue.home.createOrEditLabel');
    await metadataValueUpdatePage.cancel();
  });

  it('should create and save MetadataValues', async () => {
    const nbButtonsBeforeCreate = await metadataValueComponentsPage.countDeleteButtons();

    await metadataValueComponentsPage.clickOnCreateButton();

    await promise.all([
      metadataValueUpdatePage.setValueInput('value'),
      metadataValueUpdatePage.objectTypeSelectLastOption(),
      metadataValueUpdatePage.setObjectIdInput('5'),
      metadataValueUpdatePage.metadataFieldSelectLastOption()
    ]);

    expect(await metadataValueUpdatePage.getValueInput()).to.eq('value', 'Expected Value value to be equals to value');
    expect(await metadataValueUpdatePage.getObjectIdInput()).to.eq('5', 'Expected objectId value to be equals to 5');

    await metadataValueUpdatePage.save();
    expect(await metadataValueUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await metadataValueComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last MetadataValue', async () => {
    const nbButtonsBeforeDelete = await metadataValueComponentsPage.countDeleteButtons();
    await metadataValueComponentsPage.clickOnLastDeleteButton();

    metadataValueDeleteDialog = new MetadataValueDeleteDialog();
    expect(await metadataValueDeleteDialog.getDialogTitle()).to.eq('cvsApp.metadataValue.delete.question');
    await metadataValueDeleteDialog.clickOnConfirmButton();

    expect(await metadataValueComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
