// ----------------------------------------------------------------------------
// Copyright 2006, Martin D. Flynn
// All rights reserved
// ----------------------------------------------------------------------------
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
// http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// ----------------------------------------------------------------------------
// Description:
//  Partial implementation of a ClientPacketHandler
// ----------------------------------------------------------------------------
// Change History:
//  2006/03/26  Martin D. Flynn
//      Initial release
// ----------------------------------------------------------------------------
package org.opendmtp.util;

import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.event.*;
import javax.net.ssl.*;
import javax.net.*;
import javax.net.*;

public abstract class AbstractClientPacketHandler
    implements ClientPacketHandler
{
    
    // ------------------------------------------------------------------------

    private InetAddress inetAddr = null;
    private boolean isTCP = true;
    private boolean isTextPackets = false;
    
    protected boolean isTextPackets() 
    {
        return this.isTextPackets;
    }
    
    // ------------------------------------------------------------------------

    public void sessionStarted(InetAddress inetAddr, boolean isTCP, boolean isText) 
    {
        this.inetAddr = inetAddr;
        this.isTCP = isTCP;
        this.isTextPackets = isText;
    }
    
    // ------------------------------------------------------------------------

    public InetAddress getInetAddress()
    {
        return this.inetAddr;
    }
    
    public String getHostAddress()
    {
        String ipAddr = (this.inetAddr != null)? this.inetAddr.getHostAddress() : null;
        return ipAddr;
    }
    
    // ------------------------------------------------------------------------

    public int getActualPacketLength(byte packet[], int packetLen) 
    {
        return this.isTextPackets? -1 : packetLen;
    }
    
    // ------------------------------------------------------------------------

    public abstract byte[] getHandlePacket(byte cmd[]) 
        throws Exception;
    
    // ------------------------------------------------------------------------

    public boolean terminateSession() 
    {
        return true; // always terminate by default
    }
    
    public void sessionTerminated(Throwable err, long readCount, long writeCount)
    {
        // do nothing
    }
    
    // ------------------------------------------------------------------------

}
