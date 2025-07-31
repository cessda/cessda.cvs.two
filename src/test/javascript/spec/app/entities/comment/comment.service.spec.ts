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
import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { CommentService } from 'app/entities/comment/comment.service';
import { Comment } from 'app/shared/model/comment.model';

describe('Service Tests', () => {
  describe('Comment Service', () => {
    let injector: TestBed;
    let service: CommentService;
    let httpMock: HttpTestingController;
    let elemDefault: Comment;
    let expectedResult: Comment | Comment[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.inject(CommentService);
      httpMock = injector.inject(HttpTestingController);
      currentDate = moment();

      elemDefault = {
        id: 0,
        info: 'AAAAAAA',
        content: 'AAAAAAA',
        userId: 0,
        dateTime: currentDate,
        versionId: 0,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = [
          Object.assign(
            {
              dateTime: currentDate.format(DATE_TIME_FORMAT),
            },
            elemDefault,
          ),
        ];

        service.findAllByVersion(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject([elemDefault]);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
