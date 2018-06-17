/*
 * Copyright 2018 org.dpr & croger
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

package diskong.rip;

public class RipperException extends Throwable {
    private String messageCode;
    public RipperException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RipperException(String s) {
    super(s);
    }

    public String getMessageCode() {
        return messageCode;
    }

    public RipperException(String error, String info, int exitCode) {
        super(error + "\n" + info);
        //TODO: i18n
        messageCode = error;

    }
}