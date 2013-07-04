#!/bin/bash
kill $(cat ~/.ot/server_data/ot.pid)
rm -f ~/.ot/server_data/ot.pid
