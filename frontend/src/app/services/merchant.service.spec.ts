import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MerchantService } from './merchant.service';
import { MerchantDto } from '../models/merchant-dto.model';
import { InviteMerchantsDto } from '../models/invite-merchants-dto.model';

describe('MerchantService', () => {
	let service: MerchantService;
	let httpMock: HttpTestingController;
	const environmentMock = {
		production: false,
		envName: 'dev',
		apiPath: '/api'
	};

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
			providers: [MerchantService, { provide: 'env', useValue: environmentMock }]
		});

		service = TestBed.inject(MerchantService);
		httpMock = TestBed.inject(HttpTestingController);
	});

	afterEach(() => {
		httpMock.verify();
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should retrieve all merchants', () => {
		const mockMerchants: MerchantDto[] = [
			{
				latitude: 10,
				longitude: 10,
				companyName: 'Merchant A',
				kvk: '12345678',
				category: 'Food',
				address: 'Zandvoort',
				contactEmail: 'domain@example.com'
			},
			{
				latitude: 10,
				longitude: 20,
				companyName: 'Merchant B',
				kvk: '12345679',
				category: 'Food',
				address: 'Zandvoort',
				contactEmail: 'domain@example.com'
			}
		];

		service.getAllMerchants().subscribe((merchants) => {
			expect(merchants.length).toBe(2);
			expect(merchants).toEqual(mockMerchants);
		});

		const req = httpMock.expectOne(`${environmentMock.apiPath}/merchant/all`);
		expect(req.request.method).toBe('GET');
		req.flush(mockMerchants);
	});

	it('should register a new merchant', () => {
		const mockMerchantDto: MerchantDto = {
			latitude: 30,
			longitude: 30,
			companyName: 'Merchant C',
			kvk: '12345680',
			category: 'Retail',
			address: 'Amsterdam',
			contactEmail: 'domain@example.com'
		};

		const mockResponse: MerchantDto = { ...mockMerchantDto };

		service.registerMerchant(mockMerchantDto).subscribe((response) => {
			expect(response).toEqual(mockResponse);
		});

		const req = httpMock.expectOne(`${environmentMock.apiPath}/merchant/register`);
		expect(req.request.method).toBe('POST');
		expect(req.request.body).toEqual(mockMerchantDto);
		req.flush(mockResponse);
	});

	it('should retrieve merchants by category', () => {
		const categoryId = 1;
		const mockMerchants: MerchantDto[] = [
			{
				latitude: 10,
				longitude: 10,
				companyName: 'Merchant A',
				kvk: '12345678',
				category: 'Food',
				address: 'Zandvoort',
				contactEmail: 'domain@example.com'
			},
			{
				latitude: 10,
				longitude: 20,
				companyName: 'Merchant B',
				kvk: '12345679',
				category: 'Food',
				address: 'Zandvoort',
				contactEmail: 'domain@example.com'
			}
		];

		service.getMerchantsByCategory(categoryId).subscribe((merchants) => {
			expect(merchants.length).toBe(2);
			expect(merchants).toEqual(mockMerchants);
		});

		const req = httpMock.expectOne(`${environmentMock.apiPath}/merchant/filter/${categoryId}`);
		expect(req.request.method).toBe('GET');
		req.flush(mockMerchants);
	});

	it('should get the merchants count', () => {
		const mockMerchantsCount = 12;

		service.countAllMerchants().subscribe((count) => {
			expect(count).toEqual(mockMerchantsCount);
		});

		const req = httpMock.expectOne(`${environmentMock.apiPath}/merchant/count/all`);
		expect(req.request.method).toBe('GET');
		req.flush(mockMerchantsCount);
	});

	it('should send invite to merchants', () => {
		const mockInviteMerchantsDto: InviteMerchantsDto = {
			emails: ['merchant1@example.com', 'merchant2@example.com'],
			message: 'You are invited to join our platform.'
		};

		service.inviteMerchants(mockInviteMerchantsDto).subscribe(() => {
			expect(true).toBeTruthy();
		});

		const req = httpMock.expectOne(`${environmentMock.apiPath}/merchant/invite`);
		expect(req.request.method).toBe('POST');
		expect(req.request.body).toEqual(mockInviteMerchantsDto);
		req.flush(null);
	});

	it('should retrieve paginated merchants', () => {
		const mockMerchants: MerchantDto[] = [
			{
				latitude: 10,
				longitude: 10,
				companyName: 'Merchant A',
				kvk: '12345678',
				category: 'Food',
				address: 'Zandvoort',
				contactEmail: 'domain@example.com'
			},
			{
				latitude: 10,
				longitude: 20,
				companyName: 'Merchant B',
				kvk: '12345679',
				category: 'Food',
				address: 'Zandvoort',
				contactEmail: 'domain@example.com'
			}
		];
		const pageIndex = 0;
		const pageSize = 10;

		service.getPaginatedMerchants(pageIndex, pageSize).subscribe((merchants) => {
			expect(merchants).toEqual(mockMerchants);
		});

		const req = httpMock.expectOne(`${environmentMock.apiPath}/merchant/paginated?page=0&size=10`);
		expect(req.request.method).toBe('GET');
		req.flush(mockMerchants);
	});

	it('should handle error on getting all merchants', () => {
		const errorMessage = 'Error fetching merchants';

		service.getAllMerchants().subscribe({
			error: (error) => {
				expect(error).toBe(errorMessage);
			}
		});

		const req = httpMock.expectOne(`${environmentMock.apiPath}/merchant/all`);
		req.flush(errorMessage, { status: 500, statusText: 'Server Error' });
	});

	it('should approve a merchant', () => {
		const mockMerchantId = 'merchant123';

		service.approveMerchant(mockMerchantId).subscribe(() => {
			expect(true).toBeTruthy();
		});

		const req = httpMock.expectOne(`${environmentMock.apiPath}/merchant/approve/${mockMerchantId}`);
		expect(req.request.method).toBe('PATCH');
		req.flush(null);
	});
});
