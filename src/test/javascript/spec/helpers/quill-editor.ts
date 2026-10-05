/*
 * Copyright © 2017-2026 CESSDA ERIC (support@cessda.eu)
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
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AbstractControl, FormControl, ReactiveFormsModule } from '@angular/forms';
import { QuillModule, QuillModules } from 'ngx-quill';
import Quill from 'quill';
// ngx-quill loads Quill only when the first editor is created, and loading it together with
// lodash-es and parchment can take longer than a test is given on a busy worker; loading it with
// the spec keeps that out of the test
import 'quill';

/*
 * Quill scrolls the selection into view whenever the user moves it, and jsdom lays nothing out, so
 * it implements neither of the range measurements that needs.
 */
if (!Range.prototype.getBoundingClientRect) {
  Range.prototype.getBoundingClientRect = () => ({ x: 0, y: 0, top: 0, right: 0, bottom: 0, left: 0, width: 0, height: 0 }) as DOMRect;
  Range.prototype.getClientRects = () => [] as unknown as DOMRectList;
}

/**
 * Hosts a real quill-editor, bound to a form control the way the application templates bind it.
 *
 * The component specs override their templates with an empty one, so nothing in them would have
 * noticed the editors coming up blank (#815). A spec renders one of these with the toolbar and the
 * form control of the component under test to check that its editor still works.
 */
@Component({
  selector: 'jhi-quill-editor-host',
  template: '<quill-editor [formControl]="control" [modules]="modules" (onEditorCreated)="created($event)"></quill-editor>',
  imports: [ReactiveFormsModule, QuillModule],
})
export class QuillEditorHostComponent {
  control: FormControl = new FormControl('');
  modules?: QuillModules;
  onCreated?: (quill: Quill) => void;

  quill?: Quill;
  private resolveReady!: () => void;
  readonly ready = new Promise<void>(resolve => (this.resolveReady = resolve));

  created(quill: Quill): void {
    this.quill = quill;
    this.onCreated?.(quill);
    this.resolveReady();
  }
}

export interface RenderedQuillEditor {
  fixture: ComponentFixture<QuillEditorHostComponent>;
  quill: Quill;
  /** The editable area, which shows what the user has written */
  content: HTMLElement;
  /** The toolbar, absent when the editor is created without one */
  toolbar: HTMLElement | null;
  /** Writes text at the end of the editor as a user would, so the change reaches the form control */
  type(text: string): void;
  /** Selects the given range and presses the toolbar control that has the given class */
  press(control: string, index: number, length: number): void;
  /** Selects the given range and picks a value from the toolbar drop-down that has the given class */
  pick(control: string, value: string, index: number, length: number): void;
}

export interface QuillEditorOptions {
  /** The control the editor is bound to, which stands in for formControlName in the template */
  control?: AbstractControl;
  modules?: QuillModules;
  /** The handler the template binds to onEditorCreated */
  onCreated?: (quill: Quill) => void;
}

/**
 * Renders a real Quill editor and waits until it has been created.
 *
 * ngx-quill loads Quill lazily and creates the editor after the first render, so the editor is
 * there only once the returned promise settles.
 */
export async function renderQuillEditor(options: QuillEditorOptions = {}): Promise<RenderedQuillEditor> {
  const fixture = TestBed.createComponent(QuillEditorHostComponent);
  const host = fixture.componentInstance;
  if (options.control) {
    host.control = options.control as FormControl;
  }
  host.modules = options.modules;
  host.onCreated = options.onCreated;

  fixture.detectChanges();
  await host.ready;
  await fixture.whenStable();

  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const quill = host.quill!;
  const element: HTMLElement = fixture.nativeElement;
  return {
    fixture,
    quill,
    content: element.querySelector('.ql-editor') as HTMLElement,
    toolbar: element.querySelector('.ql-toolbar'),
    type(text: string): void {
      quill.insertText(quill.getLength() - 1, text, 'user');
      fixture.detectChanges();
    },
    press(control: string, index: number, length: number): void {
      quill.setSelection(index, length, 'user');
      (element.querySelector(`.ql-toolbar .${control}`) as HTMLElement).click();
      fixture.detectChanges();
    },
    pick(control: string, value: string, index: number, length: number): void {
      quill.setSelection(index, length, 'user');
      const select = element.querySelector(`.ql-toolbar select.${control}`) as HTMLSelectElement;
      select.value = value;
      select.dispatchEvent(new Event('change'));
      fixture.detectChanges();
    },
  };
}

/** The toolbar controls of an editor, named by their Quill format and value, e.g. "list:ordered" */
export function toolbarControls(editor: RenderedQuillEditor): string[] {
  return Array.from(editor.toolbar?.querySelectorAll<HTMLElement>('button, select') ?? []).map(control => {
    const format = Array.from(control.classList)
      .find(c => c.startsWith('ql-'))!
      .substring('ql-'.length);
    const value = control.tagName === 'BUTTON' ? control.getAttribute('value') : null;
    return value ? `${format}:${value}` : format;
  });
}
