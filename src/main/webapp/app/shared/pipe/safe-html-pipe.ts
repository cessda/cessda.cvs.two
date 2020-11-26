import {DomSanitizer} from '@angular/platform-browser'
import {Pipe, PipeTransform} from "@angular/core";

/**
 * See https://stackoverflow.com/questions/39628007/angular2-innerhtml-binding-remove-style-attribute
 */
@Pipe({ name: 'safeHtml', pure: true})
export class SafeHtmlPipe implements PipeTransform  {
  constructor(private sanitized: DomSanitizer) {}
  transform(value: any): any {
    return this.sanitized.bypassSecurityTrustHtml(value);
  }
}
