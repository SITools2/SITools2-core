Ext.override (GeoExt.data.FeatureStore, {
    /**
	 * Sort by multiple fields in the specified order.
	 * 
	 * @param {Array}
	 *            An Array of field sort specifications, or, if ascending sort
	 *            is required on all columns, an Array of field names. A field
	 *            specification looks like:
	 * 
	 * <pre><code>
	 * {
	 *     ordersList : [ {
	 *         field : firstname,
	 *         direction : ASC
	 *     }, {
	 *         field : name
	 *         direction : DESC
	 *     } ]
	 * }
	 * 
	 * </code>
	 * 
	 */
    multiSort : function (sorters, direction) {
        this.hasMultiSort = true;
        direction = direction || "ASC";

        if (this.multiSortInfo && direction == this.multiSortInfo.direction) {
            direction = direction.toggle("ASC", "DESC");
        }

        this.multiSortInfo = {
            sorters : sorters,
            direction : direction
        };

        if (this.remoteSort) {
            // this.singleSort(sorters[0].field, sorters[0].direction);
            this.load(this.lastOptions);

        } else {
            this.applySort();
            this.fireEvent('datachanged', this);
        }
    },
    getSortState : function () {
        return this.hasMultiSort ? this.multiSortInfo : this.sortInfo;
    },

    // application du tri multiple sur le store
    load : function (options) {
        options = Ext.apply({}, options);
        this.storeOptions(options);
        if ((this.sortInfo || this.multiSortInfo) && this.remoteSort) {
            var pn = this.paramNames;
            options.params = Ext.apply({}, options.params);
            this.isInSort = true;
            var root = pn.sort;
            if (this.hasMultiSort) {
                options.params[pn.sort] = Ext.encode({
                    "ordersList" : this.multiSortInfo.sorters
                });
            } else {
                options.params[pn.sort] = Ext.encode({
                    "ordersList" : [ this.sortInfo ]
                });
            }

        }

        try {
            return this.execute('read', null, options);
        } catch (e) {
            this.handleException(e);
            return false;
        }
    }
	
});