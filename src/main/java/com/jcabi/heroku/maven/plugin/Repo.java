/**
 * Copyright (c) 2012-2013, JCabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.heroku.maven.plugin;

import com.jcabi.aspects.Immutable;
import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.io.FileUtils;

/**
 * Local Git repository.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.4
 */
@Immutable
@ToString
@EqualsAndHashCode(of = { "git", "path" })
final class Repo {

    /**
     * Git engine.
     */
    private final transient Git git;

    /**
     * Location of repository.
     */
    private final transient String path;

    /**
     * Public ctor.
     * @param engine Git engine
     * @param file   Location of repository
     */
    public Repo(@NotNull final Git engine, @NotNull final File file) {
        this.git = engine;
        this.path = file.getAbsolutePath();
    }

    /**
     * Add new file.
     * @param name    Name of it
     * @param content Content of the file to write (overwrite)
     * @throws IOException If fails
     */
    public void add(@NotNull final String name, @NotNull final String content)
        throws IOException {
        final File dir = new File(this.path);
        final File file = new File(dir, name);
        FileUtils.writeStringToFile(file, content);
        this.git.exec(dir, "add", name);
        Logger.info(
            this,
            "File %s updated, %[size]s",
            file,
            file.length()
        );
    }

    /**
     * Add new file.
     *
     * @param content The file to write (overwrite)
     * @throws IOException If fails
     */
    public void add(@NotNull final File content) throws IOException {
        this.add(content.getName(), content);
    }

    /**
     * Add new file.
     *
     * @param name Name of it
     * @param content The file to write (overwrite)
     * @throws IOException If fails
     */
    public void add(@NotNull final String name, @NotNull final File content)
        throws IOException {
        final File dir = new File(this.path);
        final File file = new File(dir, name);
        FileUtils.copyFile(content, file);
        this.git.exec(dir, "add", name);
        Logger.info(
            this,
            "File %s updated, %[size]s",
            file,
            file.length()
        );
    }

    /**
     * Commit changes and push.
     */
    public void commit() {
        final File dir = new File(this.path);
        this.git.exec(dir, "status");
        this.git.exec(
            dir,
            "commit",
            "-am",
            new Date().toString()
        );
        this.git.exec(
            dir,
            "push",
            "origin",
            "master"
        );
        Logger.info(this, "Repository commited to Heroku");
    }

}
