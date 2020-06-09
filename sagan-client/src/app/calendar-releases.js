import '../css/calendar-releases.scss'

class Config {
    constructor(releases) {
        this.today = new Date()

        // detect start and end of the timeline
        let minDate = this.today, maxDate = this.today
        releases.forEach(release => {
            minDate = minDate < release.initial ? minDate : release.initial
            maxDate = maxDate > release.endDate() ? maxDate : release.endDate()
        })
        this.endDate = new Date(`${maxDate.getFullYear()}-12-31`)
        this.displayYearsCount = maxDate.getFullYear() - minDate.getFullYear()
        this.displayYearsCount = Math.max(3, this.displayYearsCount)
        this.displayYearsCount = Math.min(7, this.displayYearsCount)
        this.startDate = new Date(`${this.endDate.getFullYear() - this.displayYearsCount}-01-01`)
        this.years = []
        for (let year = this.startDate.getFullYear(); year <= this.endDate.getFullYear(); year++) {
            this.years.push(year)
        }
    }
}

class Release {
    constructor(tableRow) {
        const tds = tableRow.querySelectorAll('td')
        this.name = tds[0].innerText;
        this.initial = this.parseDate(tds[1].innerText)
        this.endSupport = this.parseDate(tds[2].innerText)
        this.endCommercial = this.parseDate(tds[3].innerText)
        this.status = tableRow.dataset.status
    }
    parseDate(str) {
        if (!str) {
            return ''
        }
        return new Date(str)
    }
    lifetime() {
        return new Period("lifetime", this.initial, this.endDate())
    }
    activeSupport() {
        return new Period("active", this.initial, this.endSupport)
    }
    commercialSupport() {
        return new Period("commercial", this.endSupport, this.endDate())
    }
    endDate() {
        return this.endCommercial ? this.endCommercial : this.endSupport
    }
}

class Period {
    constructor(name, start, end) {
        this.name = name
        this.start = start
        this.end = end
    }
    isVisibleWithin(period) {
        return this.start < period.end && this.end > period.start
    }
    calculateStartPosition(timeline) {
        if (timeline.start > this.start) {
            return 0
        }
        return Math.round((this.start - timeline.start) / (timeline.end - timeline.start) * 95) // 100-5% padding
    }
    calculateWidth(timeline) {
        const end  = Math.min(timeline.end, this.end)
        return Math.round((end - this.start) / (timeline.end - timeline.start) * 95) // 100-5% padding
    }
}

const formatDate = date => {
    return date.toISOString().substring(0,10)
}

const createDiv = ({className}) => {
    const element = document.createElement('div')
    element.className = className || ''
    return element
}

const createDateDiv = (className, date) => {
    const div = createDiv({className: `date ${className}`})
    const span = document.createElement('span')
    span.append(document.createTextNode(formatDate(date)))
    div.append(span)
    return div
}

const createAxis = (timeline, config) => {
    Array.from({length: config.years.length + 1}).forEach((_, index) => {
        const container = createDiv({className: 'year'})
        const item = createDiv({className: 'label'})
        item.append(document.createTextNode(config.years[0] + index))
        container.append(item)
        timeline.querySelector('.axis').append(container)
    })
    timeline.className = `timeline t${config.years.length}`
}

const createCurrenDate = (timeline, config) => {
    const today = new Period("today", config.today, config.today);
    const period = new Period("timeline", config.startDate, config.endDate);
    const current = createDiv({className: 'current-date'})
    const currentLabel = createDiv({className: 'label'})
    currentLabel.append(document.createTextNode(formatDate(config.today)))
    current.append(currentLabel)
    current.style.left = `${today.calculateStartPosition(period)}%`
    timeline.append(current)
}

const updateLegend = (timeline) => {
    let labelsWidth = 0
    timeline.querySelectorAll('.label-release').forEach(control => {
        if (control.offsetWidth > labelsWidth) {
            labelsWidth = control.offsetWidth
        }
    })
    timeline.querySelectorAll('.label-release').forEach(control => {
        control.style.left = `-${labelsWidth + 20}px`
    })
    timeline.querySelectorAll('.head .content').forEach(control => {
        control.style.left = `-${labelsWidth + 20}px`
    })
    timeline.style.marginLeft = `${labelsWidth + 20}px`
}

const createReleases = (timeline, releases, config) => {
    const _releases = timeline.querySelector('div.releases')
    const period = new Period("timeline", config.startDate, config.endDate);
    releases.forEach(release => {
        const _releaseD = createDiv({className: 'release'})
        _releases.append(_releaseD)

        if (release.lifetime().isVisibleWithin(period)) {
            const label = createDiv({
                className: `label label-release ${release.status}`,
            })
            label.addEventListener('mouseenter', event => {
                event.target.parentElement.className = 'release active'
            })
            label.addEventListener('mouseleave', event => {
                event.target.parentElement.className = 'release'
            })
            const span = document.createElement('span')
            span.append(document.createTextNode(release.name))
            label.append(span)
            _releaseD.append(label)

            const activeSupport = release.activeSupport()
            if (activeSupport.isVisibleWithin(period)) {
                const plopActive = createDiv({className: 'plop plop-active'})
                plopActive.style.left = `${activeSupport.calculateStartPosition(period)}%`
                plopActive.style.width = `${activeSupport.calculateWidth(period)}%`
                if (release.initial > config.today) {
                    plopActive.className = 'plop plop-active coming'
                }
                plopActive.append(createDateDiv('left', activeSupport.start))
                plopActive.append(createDateDiv('right', activeSupport.end))
                _releaseD.append(plopActive)
            }
            const commercialSupport = release.commercialSupport()
            if (commercialSupport && commercialSupport.isVisibleWithin(period)) {
                const plopMigrate = createDiv({className: 'plop plop-migrate'})
                plopMigrate.style.left = `${commercialSupport.calculateStartPosition(period)}%`
                plopMigrate.style.width = `${commercialSupport.calculateWidth(period)}%`
                plopMigrate.append(createDateDiv('right', commercialSupport.end))
                _releaseD.append(plopMigrate)
            }
        }
    })
}

const createTimeline = ({calendar}) => {
    const timeline = createDiv({className: 'timeline'})
    const _releasesDiv = createDiv({className: 'releases'})
    const _axisDiv = createDiv({className: 'axis'})
    calendar.append(timeline)
    timeline.append(_releasesDiv)
    timeline.append(_axisDiv)
    return timeline
}

const createHead = ({timeline, project}) => {
    const head = createDiv({className: 'head'})
    const headContent = createDiv({className: 'content'})
    headContent.innerText = project
    head.append(headContent)
    timeline.querySelector('.releases').append(head)
}

export const CalendarReleases = {

    singleRelease: function(calendar) {
        const releases = []
        // Parsing table data
        const trs = calendar.querySelectorAll('table tbody tr')
        trs.forEach(tr => {
            releases.push(new Release(tr))
        })
        // Timeline Config
        const config = new Config(releases)
        // Create Timeline
        const timeline = createTimeline({calendar})
        // Release
        createReleases(timeline, releases, config)
        // Axis
        createAxis(timeline, config)
        // Current Date
        createCurrenDate(timeline, config)
        // Legend size
        updateLegend(timeline)
    }

}