/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.scepta.deployment.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import io.scepta.server.DeploymentServer;

/**
 * This class represents the filesystem implementation of the Deployment Server.
 *
 */
public class FilesystemDeploymentServer implements DeploymentServer {

    private static final String ZIP_EXTENSION = ".zip";
    private static final File ROOT;

    static {
        ROOT = new File(System.getProperty("user.home")+File.separator+".scepta");

        if (!ROOT.exists()) {
            if (!ROOT.mkdir()) {
                // TODO: REPORT ERROR
                System.err.println("FAILED TO CREATE SCEPTA HOME DIR: "+ROOT);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.List<String> getDeployedTags(String organization, String group) {
        java.util.List<String> ret=new java.util.ArrayList<String>();

        File dir=new File(ROOT, organization+File.separator+group);

        if (dir.exists()) {
            for (String tag : dir.list()) {
                if (tag.endsWith(ZIP_EXTENSION)) {
                    ret.add(tag.substring(0,  tag.length()-ZIP_EXTENSION.length()));
                }
            }
        } else {
            // TODO: REPORT as error?
        }

        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deploy(String organization, String group, String tag, InputStream is) {
        File dir=new File(ROOT, organization+File.separator+group);

System.out.println("DEPLOYING TO "+dir+" tag="+tag);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                // TODO: REPORT ERROR
                System.err.println("FAILED TO CREATE SCEPTA DEPLOYMENT FOLDER: "+dir);
                return;
            }
        }

        java.io.FileOutputStream fos=null;
        try {
            fos = new java.io.FileOutputStream(new File(dir, tag+ZIP_EXTENSION));

            byte[] b=new byte[10240];

            int len=is.read(b);

            while (len > 0) {
                fos.write(b, 0, len);
                len = is.read(b);
            }

        } catch (Exception e) {
            // TODO: REPORT ERROR
            e.printStackTrace();
        } finally {
            try {
                fos.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
System.out.println("DEPLOYED TO "+dir+" tag="+tag);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getDeployment(String organization, String group, String tag, OutputStream os) {
        File f=new File(ROOT, organization+File.separator+group+File.separator+tag+ZIP_EXTENSION);

        if (!f.exists()) {
            // TODO: REPORT ERROR
            throw new RuntimeException("Deployment not available");
        }

        FileInputStream fis=null;
        try {
            fis = new FileInputStream(f);

            byte[] b=new byte[10240];

            int len=fis.read(b);

            while (len > 0) {
                os.write(b, 0, len);
                len = fis.read(b);
            }

        } catch (Exception e) {
            // TODO: REPORT ERROR
            e.printStackTrace();
        } finally {
            try {
                os.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
