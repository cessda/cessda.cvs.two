import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'linkHttp'
})
export class LinkHttpPipe implements PipeTransform {
  transform(href: string): string {
    const s = href.startsWith('http://') || href.startsWith('https://') ? href : 'http://' + href;
    return s;
  }
}
