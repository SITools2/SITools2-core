function vec3() {
    
    return {
        create : function (vec) {
            var dest = new MatrixArray(3);

            if (vec) {
                dest[0] = vec[0];
                dest[1] = vec[1];
                dest[2] = vec[2];
            } else {
                dest[0] = dest[1] = dest[2] = 0;
            }

            return dest;
        },
        createFrom : function (x, y, z) {
            var dest = new MatrixArray(3);

            dest[0] = x;
            dest[1] = y;
            dest[2] = z;

            return dest;
        },
        set : function (vec, dest) {
            dest[0] = vec[0];
            dest[1] = vec[1];
            dest[2] = vec[2];

            return dest;
        },
        equal : function (a, b) {
            return a === b || (
                Math.abs(a[0] - b[0]) < FLOAT_EPSILON &&
                Math.abs(a[1] - b[1]) < FLOAT_EPSILON &&
                Math.abs(a[2] - b[2]) < FLOAT_EPSILON
            );
        },
        add : function (vec, vec2, dest) {
            if (!dest || vec === dest) {
                vec[0] += vec2[0];
                vec[1] += vec2[1];
                vec[2] += vec2[2];
                return vec;
            }

            dest[0] = vec[0] + vec2[0];
            dest[1] = vec[1] + vec2[1];
            dest[2] = vec[2] + vec2[2];
            return dest;
        },
        subtract : function (vec, vec2, dest) {
            if (!dest || vec === dest) {
                vec[0] -= vec2[0];
                vec[1] -= vec2[1];
                vec[2] -= vec2[2];
                return vec;
            }

            dest[0] = vec[0] - vec2[0];
            dest[1] = vec[1] - vec2[1];
            dest[2] = vec[2] - vec2[2];
            return dest;
        },
        multiply : function (vec, vec2, dest) {
            if (!dest || vec === dest) {
                vec[0] *= vec2[0];
                vec[1] *= vec2[1];
                vec[2] *= vec2[2];
                return vec;
            }

            dest[0] = vec[0] * vec2[0];
            dest[1] = vec[1] * vec2[1];
            dest[2] = vec[2] * vec2[2];
            return dest;
        },
        negate : function (vec, dest) {
            if (!dest) { dest = vec; }

            dest[0] = -vec[0];
            dest[1] = -vec[1];
            dest[2] = -vec[2];
            return dest;
        },
        scale : function (vec, val, dest) {
            if (!dest || vec === dest) {
                vec[0] *= val;
                vec[1] *= val;
                vec[2] *= val;
                return vec;
            }

            dest[0] = vec[0] * val;
            dest[1] = vec[1] * val;
            dest[2] = vec[2] * val;
            return dest;
        },
        normalize : function (vec, dest) {
            if (!dest) { dest = vec; }

            var x = vec[0], y = vec[1], z = vec[2],
                len = Math.sqrt(x * x + y * y + z * z);

            if (!len) {
                dest[0] = 0;
                dest[1] = 0;
                dest[2] = 0;
                return dest;
            } else if (len === 1) {
                dest[0] = x;
                dest[1] = y;
                dest[2] = z;
                return dest;
            }

            len = 1 / len;
            dest[0] = x * len;
            dest[1] = y * len;
            dest[2] = z * len;
            return dest;
        },
        cross : function (vec, vec2, dest) {
            if (!dest) { dest = vec; }

            var x = vec[0], y = vec[1], z = vec[2],
                x2 = vec2[0], y2 = vec2[1], z2 = vec2[2];

            dest[0] = y * z2 - z * y2;
            dest[1] = z * x2 - x * z2;
            dest[2] = x * y2 - y * x2;
            return dest;
        },
        length : function (vec) {
            var x = vec[0], y = vec[1], z = vec[2];
            return Math.sqrt(x * x + y * y + z * z);
        },
        squaredLength : function (vec) {
            var x = vec[0], y = vec[1], z = vec[2];
            return x * x + y * y + z * z;
        },
        dot : function (vec, vec2) {
            return vec[0] * vec2[0] + vec[1] * vec2[1] + vec[2] * vec2[2];
        },
        lerp : function (vec, vec2, lerp, dest) {
            if (!dest) { dest = vec; }

            dest[0] = vec[0] + lerp * (vec2[0] - vec[0]);
            dest[1] = vec[1] + lerp * (vec2[1] - vec[1]);
            dest[2] = vec[2] + lerp * (vec2[2] - vec[2]);

            return dest;
        },
        dist : function (vec, vec2) {
            var x = vec2[0] - vec[0],
                y = vec2[1] - vec[1],
                z = vec2[2] - vec[2];
                
            return Math.sqrt(x*x + y*y + z*z);
        },
        str : function (vec) {
            return '[' + vec[0] + ', ' + vec[1] + ', ' + vec[2] + ']';
        }
    };
};