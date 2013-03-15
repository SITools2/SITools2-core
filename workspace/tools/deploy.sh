#! /bin/bash

# =========
# Fonctions

# -------------------------------------------------
# Trace toutes les operations dans un fichier "LOG"
_log() {
    [ "${1}" = "" ] && return 0
    mess="${1}"
    if [ ${DEBUG} -eq  1 ];then
        echo "${mess}" | tee -a ${LOG}
    else
        echo "${mess}" >> ${LOG}
    fi
}

# ----------------------------
# Arret du processus "sitools"
stopSitools() {
    _log "Arret de sitools sur ${TARGET_HOST}..."
    _log "ssh ${SSH_OPTIONS} ${TARGET_USER}@${TARGET_HOST} \"${TARGET_COM} stop\""
    _log "`ssh ${SSH_OPTIONS} ${TARGET_USER}@${TARGET_HOST} \"${TARGET_COM} stop\" 2>&1`"
}

# ----------------------------------------------------------
# Sauvegarde prealable puis effacement du repertoire sitools
purgeSitools() {
    if [ "${TARGET_PURGE_DIR}" != "" ];then
        local TD=`echo ${TARGET_PURGE_DIR} | tr -d [=*=] | tr -d [=?=] | tr -d '..'`
        if [ "${TD}" != "${TARGET_PURGE_DIR}" ];then
            _log "--- ERREUR ---"
            _log "les chemins relatifs ou avec meta-caracteres sont interdits"
            _log "--- ERREUR ---"
            exit 1
        else
            TARGET_PURGE_DIR="${TD}"
            _log "Purge de ${TARGET_PURGE_DIR} sur ${TARGET_HOST}"
            local dateBkp=`date +%Y%m%d`
            _log "   - Sauvegarde prealable, veuillez patienter..."
            _log "`ssh ${SSH_OPTIONS} ${TARGET_USER}@${TARGET_HOST} \"tar cvjf ${TARGET_DIR}/sitools-${dateBkp}.tar.bz2 ${TARGET_PURGE_DIR}\" 2>&1`"
            if [ "${TARGET_PURGE_DIR}" != "" ];then
                _log "   - Effacement du repertoire ${TARGET_PURGE_DIR} ..."
                _log "`ssh ${SSH_OPTIONS} ${TARGET_USER}@${TARGET_HOST} \"\rm -rf ${TARGET_PURGE_DIR}\" 2>&1`"
            fi
        fi
    else
        _log "la variable TARGET_PURGE_DIR n'est pas renseignee. Abandon."
        exit 1
    fi
    _log "Fin de la purge"
}

# -------------------------------------------------
# Copie du repertoire local vers le serveur distant
deploySitools() {
    if [ ! -d ${ROOT_DIR}/${SITOOLS_SRC} ];then
        _log "--- ERREUR ---"
        _log "Repertoire source ${ROOT_DIR}/${SITOOLS_SRC} introuvable. Abandon."
        _log "--- ERREUR ---"
        exit 1
    else
        _log "Deploiement en cours sur ${TARGET_HOST} ..."
        cd ${ROOT_DIR}
        _log "Creation du repertoire ${TARGET_DIR} ..."
        _log "`ssh ${SSH_OPTIONS} ${TARGET_USER}@${TARGET_HOST} \"mkdir -p ${TARGET_DIR}\" 2>&1`"
        _log "tar cvf - ${SITOOLS_SRC} | ssh ${TARGET_USER}@${TARGET_HOST} \"cd ${TARGET_DIR} ; tar xvf -\" 2>&1 >/dev/null"
        tar cvf - ${SITOOLS_SRC} | ssh ${TARGET_USER}@${TARGET_HOST} "cd ${TARGET_DIR} ; tar xvf -" 2>&1 >/dev/null
        _log "Deploiement termine."
    fi
}

# ------------------------------
# Demarrage distant de "sitools"
startSitools() {
    _log "Demarrage de sitools sur ${TARGET_HOST}..."
    _log "`ssh ${SSH_OPTIONS} ${TARGET_USER}@${TARGET_HOST} \"${TARGET_COM} start\" 2>&1`"
}

# =========
# Principal
DEBUG=1

prog=`basename ${0}`
myDir=`dirname ${0}`
myPid=${$}

startDate=`date +%Y%m%d`

LOG_DIR="${myDir}/LOG"
[ ! -d ${LOG_DIR} ] && mkdir -p ${LOG_DIR}
LOG="${LOG_DIR}/${prog}-${myPid}.log"

# ------------------------------------
# Parametres de deploiement de sitools
ROOT_DIR="${HOME}"
SITOOLS_SRC="sitools-distribution"
TARGET_USER="hudson"
TARGET_HOST="linux76.silogic.fr"
TARGET_DIR="${ROOT_DIR}/${SITOOLS_SRC}-${startDate}"

TARGET_PURGE_DIR="${TARGET_DIR}/${SITOOLS_SRC}"
mustPurge='no'

TARGET_COM="${ROOT_DIR}/${SITOOLS_SRC}/snapshot/prototype/sitools"

SSH_OPTIONS="-o BatchMode=yes -n -f"
# ---------------------------------
# Debut du processus de deploiement
#stopSitools


[ "${1}" = "-p" ] && mustPurge='yes'

[ "${mustPurge}" = "yes" ] && purgeSitools

deploySitools

#startSitools

exit 0
# =============
# Fin du script
# =============
