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

import { browser, ExpectedConditions as ec, protractor, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { CommentComponentsPage, CommentDeleteDialog, CommentUpdatePage } from './comment.page-object';

const expect = chai.expect;

describe('Comment e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let commentComponentsPage: CommentComponentsPage;
  let commentUpdatePage: CommentUpdatePage;
  let commentDeleteDialog: CommentDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Comments', async () => {
    await navBarPage.goToEntity('comment');
    commentComponentsPage = new CommentComponentsPage();
    await browser.wait(ec.visibilityOf(commentComponentsPage.title), 5000);
    expect(await commentComponentsPage.getTitle()).to.eq('cvsApp.comment.home.title');
    await browser.wait(ec.or(ec.visibilityOf(commentComponentsPage.entities), ec.visibilityOf(commentComponentsPage.noResult)), 1000);
  });

  it('should load create Comment page', async () => {
    await commentComponentsPage.clickOnCreateButton();
    commentUpdatePage = new CommentUpdatePage();
    expect(await commentUpdatePage.getPageTitle()).to.eq('cvsApp.comment.home.createOrEditLabel');
    await commentUpdatePage.cancel();
  });

  it('should create and save Comments', async () => {
    const nbButtonsBeforeCreate = await commentComponentsPage.countDeleteButtons();

    await commentComponentsPage.clickOnCreateButton();

    await promise.all([
      commentUpdatePage.setInfoInput('info'),
      commentUpdatePage.setContentInput('content'),
      commentUpdatePage.setUserIdInput('5'),
      commentUpdatePage.setDateTimeInput('01/01/2001' + protractor.Key.TAB + '02:30AM'),
      commentUpdatePage.versionSelectLastOption()
    ]);

    expect(await commentUpdatePage.getInfoInput()).to.eq('info', 'Expected Info value to be equals to info');
    expect(await commentUpdatePage.getContentInput()).to.eq('content', 'Expected Content value to be equals to content');
    expect(await commentUpdatePage.getUserIdInput()).to.eq('5', 'Expected userId value to be equals to 5');
    expect(await commentUpdatePage.getDateTimeInput()).to.contain('2001-01-01T02:30', 'Expected dateTime value to be equals to 2000-12-31');

    await commentUpdatePage.save();
    expect(await commentUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await commentComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Comment', async () => {
    const nbButtonsBeforeDelete = await commentComponentsPage.countDeleteButtons();
    await commentComponentsPage.clickOnLastDeleteButton();

    commentDeleteDialog = new CommentDeleteDialog();
    expect(await commentDeleteDialog.getDialogTitle()).to.eq('cvsApp.comment.delete.question');
    await commentDeleteDialog.clickOnConfirmButton();

    expect(await commentComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
