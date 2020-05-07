import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { ResolverComponentsPage, ResolverDeleteDialog, ResolverUpdatePage } from './resolver.page-object';

const expect = chai.expect;

describe('Resolver e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let resolverComponentsPage: ResolverComponentsPage;
  let resolverUpdatePage: ResolverUpdatePage;
  let resolverDeleteDialog: ResolverDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Resolvers', async () => {
    await navBarPage.goToEntity('resolver');
    resolverComponentsPage = new ResolverComponentsPage();
    await browser.wait(ec.visibilityOf(resolverComponentsPage.title), 5000);
    expect(await resolverComponentsPage.getTitle()).to.eq('cvsApp.resolver.home.title');
    await browser.wait(ec.or(ec.visibilityOf(resolverComponentsPage.entities), ec.visibilityOf(resolverComponentsPage.noResult)), 1000);
  });

  it('should load create Resolver page', async () => {
    await resolverComponentsPage.clickOnCreateButton();
    resolverUpdatePage = new ResolverUpdatePage();
    expect(await resolverUpdatePage.getPageTitle()).to.eq('cvsApp.resolver.home.createOrEditLabel');
    await resolverUpdatePage.cancel();
  });

  it('should create and save Resolvers', async () => {
    const nbButtonsBeforeCreate = await resolverComponentsPage.countDeleteButtons();

    await resolverComponentsPage.clickOnCreateButton();

    await promise.all([
      resolverUpdatePage.setResourceIdInput('resourceId'),
      resolverUpdatePage.resourceTypeSelectLastOption(),
      resolverUpdatePage.setResourceUrlInput('resourceUrl'),
      resolverUpdatePage.resolverTypeSelectLastOption(),
      resolverUpdatePage.setResolverURIInput('resolverURI')
    ]);

    expect(await resolverUpdatePage.getResourceIdInput()).to.eq('resourceId', 'Expected ResourceId value to be equals to resourceId');
    expect(await resolverUpdatePage.getResourceUrlInput()).to.eq('resourceUrl', 'Expected ResourceUrl value to be equals to resourceUrl');
    expect(await resolverUpdatePage.getResolverURIInput()).to.eq('resolverURI', 'Expected ResolverURI value to be equals to resolverURI');

    await resolverUpdatePage.save();
    expect(await resolverUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await resolverComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Resolver', async () => {
    const nbButtonsBeforeDelete = await resolverComponentsPage.countDeleteButtons();
    await resolverComponentsPage.clickOnLastDeleteButton();

    resolverDeleteDialog = new ResolverDeleteDialog();
    expect(await resolverDeleteDialog.getDialogTitle()).to.eq('cvsApp.resolver.delete.question');
    await resolverDeleteDialog.clickOnConfirmButton();

    expect(await resolverComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
