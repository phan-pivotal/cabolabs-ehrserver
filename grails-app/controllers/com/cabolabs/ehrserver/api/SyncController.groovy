/*
 * Copyright 2011-2017 CaboLabs Health Informatics
 *
 * The EHRServer was designed and developed by Pablo Pazos Gutierrez <pablo.pazos@cabolabs.com> at CaboLabs Health Informatics (www.cabolabs.com).
 *
 * You can't remove this notice from the source code, you can't remove the "Powered by CaboLabs" from the UI, you can't remove this notice from the window that appears then the "Powered by CaboLabs" link is clicked.
 *
 * Any modifications to the provided source code can be stated below this notice.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cabolabs.ehrserver.api

import com.cabolabs.ehrserver.sync.SyncParserService
import grails.converters.*

import net.kaleidos.grails.plugin.security.stateless.annotation.SecuredStateless
import org.codehaus.groovy.grails.web.json.JSONObject
import groovy.json.JsonSlurper

import com.cabolabs.ehrserver.reporting.ActivityLog
import com.cabolabs.ehrserver.ResourceService
import com.cabolabs.security.*

/**
 * Controller that receives the sync operations
 */
class SyncController {

   static allowedMethods = [syncAccount: "POST",
                            syncEhr: "POST",
                            syncOpt: "POST",
                            syncContribution: "POST",
                            syncQuery: "POST"
                           ]

   def syncParserService
   def resourceService


   @SecuredStateless
   def syncAccount()
   {
      println "syncAccount " //+ request.securityStatelessMap.extradata

      // TODO: check if already exists
      // TOOD: check if this is an update, that should be a PUT, OK result should be 200
      //println request.JSON

      // account with contact user and list of organizations
      def account = syncParserService.fromJSONAccount(request.JSON.account)

      if (!account) println "NULL!!!!"

      if (!account.save(flush:true))
      {
         // TODO: handle error
         println account.errors.allErrors
      }

      def alog = new ActivityLog(
         username:        request.securityStatelessMap.username, // can be null
         organizationUid: null, /* sync is for the system not for a specific org */
         action:          controllerName+':'+actionName,
         objectId:        account.id,
         objectUid:       account.uid,
         remoteAddr:      request.remoteAddr,
         clientIp:        request.getHeader("Client-IP"), // can be null
         xForwardedFor:   request.getHeader("X-Forwarded-For"), // can be null
         referer:         request.getHeader('referer'), // can be null
         requestURI:      request.forwardURI,
         matchedURI:      request.requestURI,
         sessionId:       session.id)


      // TODO: file log failure
      if (!alog.save()) println "activity log is not saving "+ alog.errors.toString()


      // TODO: structure for the response
      render( status:201, text:[message: 'account synced OK'] as JSON, contentType:"application/json", encoding:"UTF-8")
   }

   def syncEhr()
   {
      println "syncEhr"

      def ehr = syncParserService.fromJSONEhr(request.JSON.ehr)

      if (!ehr.save(flush:true))
      {
         // TODO: handle error
         println ehr.errors.allErrors
      }

      def alog = new ActivityLog(
         username:        request.securityStatelessMap.username, // can be null
         organizationUid: null, /* sync is for the system not for a specific org */
         action:          controllerName+':'+actionName,
         objectId:        ehr.id,
         objectUid:       ehr.uid,
         remoteAddr:      request.remoteAddr,
         clientIp:        request.getHeader("Client-IP"), // can be null
         xForwardedFor:   request.getHeader("X-Forwarded-For"), // can be null
         referer:         request.getHeader('referer'), // can be null
         requestURI:      request.forwardURI,
         matchedURI:      request.requestURI,
         sessionId:       session.id)


      // TODO: file log failure
      if (!alog.save()) println "activity log is not saving "+ alog.errors.toString()

      // TODO: structure for the response
      render( status:201, text:[message: 'ehr synced OK'] as JSON, contentType:"application/json", encoding:"UTF-8")
   }

   def syncOpt()
   {
      println "syncOpt"

      def optIndex = syncParserService.toJSONOpt(request.JSON.template)

      if (!optIndex.save(flush:true))
      {
         // TODO: handle error
         println optIndex.errors.allErrors
      }

      def alog = new ActivityLog(
         username:        request.securityStatelessMap.username, // can be null
         organizationUid: null, /* sync is for the system not for a specific org */
         action:          controllerName+':'+actionName,
         objectId:        optIndex.id,
         objectUid:       optIndex.uid,
         remoteAddr:      request.remoteAddr,
         clientIp:        request.getHeader("Client-IP"), // can be null
         xForwardedFor:   request.getHeader("X-Forwarded-For"), // can be null
         referer:         request.getHeader('referer'), // can be null
         requestURI:      request.forwardURI,
         matchedURI:      request.requestURI,
         sessionId:       session.id)


      // TODO: file log failure
      if (!alog.save()) println "activity log is not saving "+ alog.errors.toString()

      // TODO: structure for the response
      render( status:201, text:[message: 'opt synced OK'] as JSON, contentType:"application/json", encoding:"UTF-8")
   }

   def syncContribution()
   {
      println "syncContribution"

      // TODO: should catch validation errores and retrieve them to the client
      //println request.JSON // changes the order of the objects!!!!
      //def jo = new JSONObject(request.reader.text) // the issue is the JSONObject used by grails it is unordered!
      //println jo

      LinkedHashMap json = new JsonSlurper().parseText(request.reader.text)
      def jo = new JSONObject(json)
      println jo
      println "-----------"


      def contribution = syncParserService.fromJSONContribution(jo)

      if (!contribution.save(flush:true))
      {
         // TODO: handle error
         println contribution.errors.allErrors
      }

      def alog = new ActivityLog(
         username:        request.securityStatelessMap.username, // can be null
         organizationUid: null, /* sync is for the system not for a specific org */
         action:          controllerName+':'+actionName,
         objectId:        contribution.id,
         objectUid:       contribution.uid,
         remoteAddr:      request.remoteAddr,
         clientIp:        request.getHeader("Client-IP"), // can be null
         xForwardedFor:   request.getHeader("X-Forwarded-For"), // can be null
         referer:         request.getHeader('referer'), // can be null
         requestURI:      request.forwardURI,
         matchedURI:      request.requestURI,
         sessionId:       session.id)


      // TODO: file log failure
      if (!alog.save()) println "activity log is not saving "+ alog.errors.toString()

      // TODO: structure for the response
      render( status:201, text:[message: 'contribution synced OK'] as JSON, contentType:"application/json", encoding:"UTF-8")
   }

   @SecuredStateless
   def syncQuery()
   {
      LinkedHashMap json = new JsonSlurper().parseText(request.reader.text)
      def jo = new JSONObject(json)
      println jo
      println "-----------"

      def query = syncParserService.fromJSONQuery(jo)

      if (!query.save(flush:true))
      {
         // TODO: handle error
         println query.errors.allErrors
      }
      else
      {
         if (!query.isPublic)
         {
            resourceService.shareQuery(query, Organization.findByUid(query.organizationUid))
         }
      }

      def alog = new ActivityLog(
         username:        request.securityStatelessMap.username, // can be null
         organizationUid: null, /* sync is for the system not for a specific org */
         action:          controllerName+':'+actionName,
         objectId:        query.id,
         objectUid:       query.uid,
         remoteAddr:      request.remoteAddr,
         clientIp:        request.getHeader("Client-IP"), // can be null
         xForwardedFor:   request.getHeader("X-Forwarded-For"), // can be null
         referer:         request.getHeader('referer'), // can be null
         requestURI:      request.forwardURI,
         matchedURI:      request.requestURI,
         sessionId:       session.id)


      // TODO: file log failure
      if (!alog.save()) println "activity log is not saving "+ alog.errors.toString()

      // TODO: structure for the response
      render( status:201, text:[message: 'query synced OK'] as JSON, contentType:"application/json", encoding:"UTF-8")
   }

   def syncQuery2()
   {
      render "xxx2"
   }
}
