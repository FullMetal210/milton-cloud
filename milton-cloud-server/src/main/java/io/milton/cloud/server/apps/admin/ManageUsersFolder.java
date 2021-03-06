/*
 * Copyright (C) 2012 McEvoy Software Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.milton.cloud.server.apps.admin;

import io.milton.cloud.server.apps.orgs.OrganisationFolder;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.milton.vfs.db.Organisation;
import io.milton.vfs.db.Profile;
import io.milton.cloud.server.db.utils.UserDao;
import io.milton.cloud.server.web.*;
import io.milton.cloud.server.web.templating.HtmlTemplater;
import io.milton.resource.AccessControlledResource.Priviledge;
import io.milton.http.Auth;
import io.milton.http.FileItem;
import io.milton.http.Range;
import io.milton.principal.Principal;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.NotFoundException;
import io.milton.resource.GetableResource;
import io.milton.resource.PostableResource;
import io.milton.vfs.db.utils.SessionManager;

import static io.milton.context.RequestContext._;
import io.milton.resource.Resource;

/**
 *
 * @author brad
 */
public class ManageUsersFolder extends AbstractCollectionResource implements GetableResource, PostableResource {

    private static final Logger log = LoggerFactory.getLogger(ManageUsersFolder.class);
    
    private final String name;
    private final CommonCollectionResource parent;
    private final Organisation organisation;
    private JsonResult jsonResult;
    private List<Profile> searchResults;

    public ManageUsersFolder(String name, Organisation organisation, CommonCollectionResource parent) {
        this.organisation = organisation;
        this.parent = parent;
        this.name = name;
    }

    public String getTitle() {
        return "Manage users";
    }
    
    @Override
    public Resource child(String childName) throws NotAuthorizedException, BadRequestException {
        if( childName.equals("new")) {
            return new ManageUserPage(childName, null, parent);
        }
        Long profileId = Long.parseLong(childName);
        Profile p = (Profile) SessionManager.session().get(Profile.class, profileId);
        if(  p == null ) {
            return null;
        }
        return new ManageUserPage(p.getName(), p, parent);
    }

       
    @Override
    public String processForm(Map<String, String> parameters, Map<String, FileItem> files) throws BadRequestException, NotAuthorizedException, ConflictException {
        throw new UnsupportedOperationException("Not supported yet.");
    }    
    
    
    @Override
    public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotAuthorizedException, BadRequestException, NotFoundException {        
        
        UserDao userDao = _(UserDao.class);
        
        OrganisationFolder orgFolder = WebUtils.findParentOrg(this);
        Organisation org = orgFolder.getOrganisation();
        String q = params.get("q");
        if( q != null && q.length() > 0 ) {            
            searchResults = userDao.search(q, org, SessionManager.session()); // find the given user in this organisation
        } else {
            searchResults = userDao.listProfiles(org, SessionManager.session()); // find the given user in this organisation
        }
        _(HtmlTemplater.class).writePage("admin","admin/manageUsers", this, params, out);
    }

    
    @Override
    public boolean isDir() {
        return false;
    }

    @Override
    public CommonCollectionResource getParent() {
        return parent;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Date getModifiedDate() {
        return null;
    }

    @Override
    public Date getCreateDate() {
        return null;
    }

    @Override
    public Map<Principal, List<Priviledge>> getAccessControlList() {
        return null;
    }


    @Override
    public Long getMaxAgeSeconds(Auth auth) {
        return null;
    }

    @Override
    public String getContentType(String accepts) {
        return "text/html";
    }

    @Override
    public Long getContentLength() {
        return null;
    }

    public List<Profile> getSearchResults() {
        return searchResults;
    }
    
    @Override
    public Organisation getOrganisation() {
        return organisation;
    }
    
    @Override
    public boolean is(String type) {
        if( type.equals("userAdmin")) {
            return true;
        }
        return super.is(type);
    }

    @Override
    public List<? extends Resource> getChildren() throws NotAuthorizedException, BadRequestException {
        throw new UnsupportedOperationException("Not supported yet.");
    }        
}
