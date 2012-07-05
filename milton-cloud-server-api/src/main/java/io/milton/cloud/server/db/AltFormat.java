/*
 * Copyright 2012 McEvoy Software Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.milton.cloud.server.db;

import io.milton.vfs.db.utils.DbUtils;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

/**
 * Makes a connection between the hash for a primary file and an alternative format
 * for that file
 *
 * @author brad
 */
@javax.persistence.Entity
@Table(
uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "sourceHash"})}
)
public class AltFormat implements Serializable {
    
    public static AltFormat find(long sourceHash, String name, Session session) {
        Criteria crit = session.createCriteria(AltFormat.class);
        crit.add(Expression.and(Expression.eq("sourceHash", sourceHash), Expression.eq("name", name)));        
        return DbUtils.unique(crit);
    }    
    
    public static AltFormat insertIfOrUpdate(String name, long sourceHash, long altHash, Session session) {
        AltFormat f = find(sourceHash, name, session);
        if( f != null ) {
            if( f.getAltHash() == altHash) {
                return f;
            }
        } else {
            f = new AltFormat();
        }        
        f.setAltHash(altHash);
        f.setName(name);
        f.setSourceHash(sourceHash);
        session.save(f);
        return f;
    }
    
    private long id;
    private long sourceHash; // the hash of the file from which the alternative was derived
    private String name; // the name of the alt format: eg thumb-120x120.png
    private long altHash; // the hash of the alternative file
    
    @Id
    @GeneratedValue    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(nullable=false)
    public long getSourceHash() {
        return sourceHash;
    }

    public void setSourceHash(long sourceHash) {
        this.sourceHash = sourceHash;
    }

    @Column(nullable=false)
    public long getAltHash() {
        return altHash;
    }

    public void setAltHash(long altHash) {
        this.altHash = altHash;
    }

    @Column(nullable=false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
