export interface IJobApplications {
  id?: number;
  name?: string;
  surname?: string;
  email?: string;
  phone?: string;
  area?: string;
  message?: string | null;
  cvContentType?: string;
  cv?: string;
}

export class JobApplications implements IJobApplications {
  constructor(
    public id?: number,
    public name?: string,
    public surname?: string,
    public email?: string,
    public phone?: string,
    public area?: string,
    public message?: string | null,
    public cvContentType?: string,
    public cv?: string
  ) {}
}

export function getJobApplicationsIdentifier(jobApplications: IJobApplications): number | undefined {
  return jobApplications.id;
}
