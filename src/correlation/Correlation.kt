package correlation

import kotlin.math.absoluteValue


object Correlation {

    fun coefficient(first: Array<Double>, second: Array<Double>): Double {
        var firstSum = 0.0
        var secondSum = 0.0

        for (index in 0 until Math.max(first.size, second.size)) {
            if (index < first.size) {
                firstSum += Math.pow(first[index], 2.0)
            }

            if (index < second.size) {
                secondSum += Math.pow(second[index], 2.0)
            }
        }

        return Math.sqrt(firstSum * secondSum);
    }

    fun values(first: Array<Double>, second: Array<Double>, coefficient: Double = 1.0) = correlation(first = first,
        second = second, coefficient = coefficient)

    fun value(first: Array<Double>, second: Array<Double>) = value(
        0,
        Math.max(first.size, second.size),
        first,
        second
    )

    fun crossValuesArray(
        sample: Array<Double>,
        source: Array<Double>,
        emptyValue: Double,
        corrCoefficient: Double
    ): Array<Array<Double>> {
        // iterations count
        val iteration = 1 - sample.size until sample.size + source.size + 1

        // the empty bids from sample signal are filling with defined empty values
        val equalizedSample = Array(source.size) { index ->
            if (sample.size > index) sample[index] else emptyValue
        }

        // returns new array of correlated values
        return Array(size = iteration.last) { parentIndex ->
            // shift the sample signal
            val shiftedSample = arrayShiftLinear(
                equalizedSample,
                parentIndex + iteration.first,
                true,
                emptyValue
            )
            // returns correlation values
            return@Array values(shiftedSample, source, corrCoefficient)
        }
    }

    fun crossValues(
        sample: Array<Double>,
        source: Array<Double>,
        emptyValue: Double,
        corrCoefficient: Double
    ): Array<Double> {
        val iteration = 1 - sample.size until sample.size + source.size + 1

        // the empty bids from sample signal are filling with defined empty values
        val equalizedSample = Array(source.size) { index ->
            if (sample.size > index) sample[index] else emptyValue
        }

        // returns new array of correlated values
        return Array(size = iteration.last) { parentIndex ->
            // shift the sample signal
            val shiftedSample = arrayShiftLinear(
                equalizedSample,
                parentIndex + iteration.first,
                true,
                emptyValue
            )

            // returns correlation values
            return@Array value(shiftedSample, source) / corrCoefficient
        }
    }

    fun circularValuesArray(
        sample: Array<Double>,
        source: Array<Double>,
        emptyValue: Double
    ): Array<Array<Double>> {
        // the empty bids from sample signal are filling with defined empty values
        val iteration = 0 until source.size

        // the empty bids from sample signal are filling with defined empty values
        val equalizedSample = Array(source.size) { index ->
            if (sample.size > index) sample[index] else emptyValue
        }

        // returns new array of correlated values
        return Array(size = iteration.last + 1) { parentIndex ->
            // shift the sample signal, and put the shifted part from the start of the sample signal
            val shiftedSample = arrayShiftCircular(equalizedSample, parentIndex + iteration.first, true)

            // returns correlation values
            return@Array values(shiftedSample, source)
        }
    }


    fun circularValuesArrayAsync(sample: Array<Double>, source: Array<Double>, emptyValue: Double): Array<Array<Double>> {
        // իտերացիաների քանակ
        val iteration = 0 until source.size

        // sample արգումենտի դատարկ բիտերը լրացվում են 0-ով
        val equalizedSample = Array(source.size) { index ->
            if (sample.size > index) sample[index] else emptyValue
        }

        // վերադարձնում է նոր զանգված, որի տարրերը կորելացիայի արդյունքները պարունակող զանգվածներ են
        return Array(size = iteration.last + 1) { parentIndex ->
            // կատարվում է source զանգվածի շրջանաձև տեղաշարժ
            val shiftedSample = arrayShiftCircular(equalizedSample, parentIndex + iteration.first, true)

            return@Array values(shiftedSample, source)
        }
    }

    fun circularValues(
        sample: Array<Double>,
        source: Array<Double>,
        emptyValue: Double,
        corrCoefficient: Double
    ): Array<Double> {
        val iteration = 0 until source.size

        // creating a new array with source.size
        val equalizedSample = Array(source.size) { index ->
            if (sample.size > index) sample[index] else emptyValue
        }

        return Array(size = iteration.last + 1) { parentIndex ->
            val shiftedSample = arrayShiftCircular(equalizedSample, parentIndex + iteration.first, true)

            return@Array value(shiftedSample, source) / corrCoefficient
        }
    }

    private fun correlation(
        start: Int = 0, // first index
        end: Int = 0, // second index
        first: Array<Double>, // first signal
        second: Array<Double>, // second signal
        result: Array<Double> = Array(2 * Math.max(first.size, second.size) - 1) { 0.0 }, // output vales
        coefficient: Double = 1.0 // coefficient for average values
    ): Array<Double> {

        val step = start + end + 1 // definition of the current phase
        val length = Math.max(first.size, second.size) // the maximum length of imputed signals

        // definition indexes of the next step
        var de = end
        var ds = start

        // if the leading pointer is not achieved to the end of signal
        // if true, the first pointers value will be incremented
        if ((length - 1 - step) >= 0) {
            de++
        } else
        // otherwise the second pointers value will be incremented
            if ((length - step) <= 0) {
                ds++
            }

        // check, if the function is reach the end of correlation
        if (step == 2 * length) {
            // done, return the correlation values
            return result
        } else {
            // otherwise calculates the correlation value for current phase and divide it by coefficient
            val sum = value(start, end, first, second)
            result[step - 1] = sum / coefficient

            // recursively start the next phase
            return correlation(ds, de, first, second, result, coefficient)
        }
    }

    private fun value(start: Int, end: Int, first: Array<Double>, second: Array<Double>): Double {
        //  fist pointer's index of the second signal
        var i = start
        // fist pointer's index of the second signal
        var j = Math.max(first.size, second.size) - (end - start) - 1

        var sum = 0.0 // summary holder

        // Calculates the correlation value, for n+1 position the correlation value will be
        // rxy [n+1] = X2 Y n-1 + X3 Y n-2+ … + Xn-1 Y 2 + Xn Y 1

        for (d in 0..(end - start)) {

            val firstValue = if (first.size > i) first[i] else 0.0
            val secondValue = if (second.size > i) second[i] else 0.0

            sum += firstValue * secondValue

            i++
            j++
        }

        return sum
    }

    public fun arrayShift(
        array: Array<Double>,
        shift: Int,
        circular: Boolean = false,
        copyArray: Boolean = false,
        emptyValue: Double = 0.0
    ): Array<Double> {
        if (array.isEmpty() || array.size == 1 || shift == 0) {
            return array
        }

        val toRight = shift > 0

        val shiftedArray = if (copyArray) array.copyOf() else array

        for (step in 0 until shift.absoluteValue) {

            fun tailValue() = if (toRight) shiftedArray[array.size - 1] else shiftedArray[0]

            val tail = if (circular) tailValue() else emptyValue

            val iteration = if (toRight) array.size - 1 downTo 0 else 0 until array.size

            for (i in iteration) {
                if (toRight) {
                    if (i - 1 >= 0) {
                        shiftedArray[i] = shiftedArray[i - 1]
                    } else {
                        shiftedArray[i] = tail
                    }
                } else {
                    if (i + 1 < array.size) {
                        shiftedArray[i] = shiftedArray[i + 1]
                    } else {
                        shiftedArray[i] = tail
                    }
                }
            }
        }

        return shiftedArray
    }

    private fun arrayShiftCircular(array: Array<Double>, shift: Int, copyArray: Boolean = false) = arrayShift(
        array,
        shift,
        true,
        copyArray
    )

    private fun arrayShiftLinear(array: Array<Double>, shift: Int, copyArray: Boolean = false, emptyValue: Double) = arrayShift(array, shift, false, copyArray, emptyValue)
}

