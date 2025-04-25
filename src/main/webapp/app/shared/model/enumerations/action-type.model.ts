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
export const enum ActionType {
  CREATE_CV = 'CREATE_CV',
  EDIT_CV = 'EDIT_CV',
  EDIT_DDI_CV = 'EDIT_DDI_CV',
  EDIT_IDENTITY_CV = 'EDIT_IDENTITY_CV',
  EDIT_VERSION_INFO_CV = 'EDIT_VERSION_INFO_CV',
  EDIT_NOTE_CV = 'EDIT_NOTE_CV',
  DELETE_CV = 'DELETE_CV',
  ADD_TL_CV = 'ADD_TL_CV',
  FORWARD_CV_SL_STATUS_REVIEW = 'FORWARD_CV_SL_STATUS_REVIEW',
  FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE = 'FORWARD_CV_SL_STATUS_READY_TO_TRANSLATE',
  FORWARD_CV_SL_STATUS_PUBLISH = 'FORWARD_CV_SL_STATUS_PUBLISH',
  FORWARD_CV_TL_STATUS_REVIEW = 'FORWARD_CV_TL_STATUS_REVIEW',
  FORWARD_CV_TL_STATUS_READY_TO_PUBLISH = 'FORWARD_CV_TL_STATUS_READY_TO_PUBLISH',
  CREATE_CODE = 'CREATE_CODE',
  EDIT_CODE = 'EDIT_CODE',
  REORDER_CODE = 'REORDER_CODE',
  DEPRECATE_CODE = 'DEPRECATE_CODE',
  DELETE_CODE = 'DELETE_CODE',
  ADD_TL_CODE = 'ADD_TL_CODE',
  EDIT_TL_CODE = 'EDIT_TL_CODE',
  DELETE_TL_CODE = 'DELETE_TL_CODE',
  CREATE_NEW_CV_SL_VERSION = 'CREATE_NEW_CV_SL_VERSION',
  CREATE_NEW_CV_TL_VERSION = 'CREATE_NEW_CV_TL_VERSION',
}
