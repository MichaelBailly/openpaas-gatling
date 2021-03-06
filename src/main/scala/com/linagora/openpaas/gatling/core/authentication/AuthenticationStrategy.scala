/** **************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0                 *
 * *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 * ************************************************************** */
package com.linagora.openpaas.gatling.core.authentication

sealed trait AuthenticationStrategy

object AuthenticationStrategy {
  case object Basic extends AuthenticationStrategy
  case object LemonLDAP extends AuthenticationStrategy
  case object OIDC extends AuthenticationStrategy
  case object PKCE extends AuthenticationStrategy
  case object PKCE_WITH_CAS extends AuthenticationStrategy

  def fromConfiguration(key: String): Option[AuthenticationStrategy] = key match {
    case "basic" => Some(Basic)
    case "lemonldap" => Some(LemonLDAP)
    case "oidc" => Some(OIDC)
    case "pkce" => Some(PKCE)
    case "pkce_with_cas" => Some(PKCE_WITH_CAS)
    case _ => None
  }
}
